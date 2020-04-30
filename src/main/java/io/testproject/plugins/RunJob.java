package io.testproject.plugins;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.testproject.constants.Constants;
import io.testproject.helpers.*;
import io.testproject.model.*;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RunJob extends Builder implements SimpleBuildStep {

    //region Private members
    private ApiHelper apiHelper;
    private String executionId;
    private Timer stateTimer;
    private boolean aborting;

    private @Nonnull
    String projectId;

    private @Nonnull
    String jobId;

    private String agentId;

    private int waitJobFinishSeconds;

    private String executionParameters;
    //endregion

    //region Setters & Getters
    @Nonnull
    public String getProjectId() {
        return projectId;
    }

    @DataBoundSetter
    public void setProjectId(@Nonnull String projectId) {
        this.projectId = projectId;
    }

    @Nonnull
    public String getJobId() {
        return jobId;
    }

    @DataBoundSetter
    public void setJobId(@Nonnull String jobId) {
        this.jobId = jobId;
    }

    public int getWaitJobFinishSeconds() {
        return waitJobFinishSeconds;
    }

    @DataBoundSetter
    public void setWaitJobFinishSeconds(int waitJobFinishSeconds) {
        this.waitJobFinishSeconds = waitJobFinishSeconds;
    }

    public String getAgentId() {
        return agentId;
    }

    @DataBoundSetter
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getExecutionParameters() {
        return executionParameters;
    }

    @DataBoundSetter
    public void setExecutionParameters(String executionParameters) {
        this.executionParameters = executionParameters;
    }

    //endregion

    //region Constructors
    public RunJob() {
        this.projectId = "";
        this.jobId = "";
        this.agentId = "";
        this.waitJobFinishSeconds = 0;
        this.executionParameters = "";
    }

    @DataBoundConstructor
    public RunJob(@Nonnull String projectId, @Nonnull String jobId, String agentId, int waitJobFinishSeconds, String executionParameters) {
        this.projectId = projectId;
        this.jobId = jobId;
        this.agentId = agentId;
        this.waitJobFinishSeconds = waitJobFinishSeconds;
        this.executionParameters = executionParameters;
    }
    //endregion

    private void init() {
        this.apiHelper = new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey());
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override // SimpleBuildStep implementation
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws AbortException {
        try {
            LogHelper.SetLogger(taskListener.getLogger(), PluginConfiguration.DESCRIPTOR.isVerbose());
            LogHelper.Info("Sending a job run command to TestProject");

            if (StringUtils.isEmpty(getProjectId()))
                throw new AbortException("The project id cannot be empty");

            if (StringUtils.isEmpty(getJobId()))
                throw new AbortException("The job id cannot be empty");

            triggerJob(run.getNumber());
        } catch (Exception e) {
            throw new AbortException(e.getMessage());
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    private void triggerJob(Object buildNumber) throws Exception {
        try {
            init();

            String logMsg = StringUtils.isEmpty(getAgentId())
                    ? String.format("Starting TestProject job %s under project %s using the default agent...", this.jobId, this.projectId)
                    : String.format("Starting TestProject job %s under project %s using agent %s...", this.jobId, this.projectId, this.agentId);
            LogHelper.Info(logMsg);

            HashMap<String, Object> headers = new HashMap<>();
            headers.put(Constants.CI_NAME_HEADER, Constants.CI_NAME);
            headers.put(Constants.CI_BUILD_HEADER, buildNumber);

            ApiResponse<ExecutionResponseData> response = apiHelper.Post(
                    String.format(Constants.TP_RUN_JOB_URL, projectId, jobId),
                    headers,
                    null,
                    generateRequestBody(),
                    ExecutionResponseData.class);

            if (response.isSuccessful()) {
                if (response.getData() != null) {
                    ExecutionResponseData data = response.getData();
                    executionId = data.getId();
                    LogHelper.Info("Execution id: " + executionId);

                    waitForJobFinish();
                }
            } else {
                throw new AbortException(response.generateErrorMessage("Unable to trigger TestProject job"));
            }
        } catch (InterruptedException ie) {
            LogHelper.Error(ie);
            if (this.executionId != null) {
                abortExecution();
            }
        }
    }

    private JsonObject generateRequestBody() throws AbortException {
        JsonObject executionData = null;

        try {
            executionData = SerializationHelper.fromJson(executionParameters, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new AbortException(e.getMessage());
        }

        // In case the user has selected an agent from the dropdown list, use it in the executionParameters object
        if (!StringUtils.isEmpty(getAgentId()))
            executionData.addProperty("agentId", getAgentId());

        return executionData;
    }

    private void waitForJobFinish() throws IOException, InterruptedException {
        if (this.waitJobFinishSeconds == 0) {
            LogHelper.Info("Will not wait for execution to finish");
            LogHelper.Info(String.format("Job %s under project %s was started successfully", this.jobId, this.projectId));
            return;
        }

        Calendar jobTimeout = Calendar.getInstance();
        jobTimeout.add(Calendar.SECOND, waitJobFinishSeconds);
        LogHelper.Info("Will wait " + waitJobFinishSeconds + " seconds for execution to finish (not later than " + jobTimeout.getTime().toString() + ")");

        // Waiting for execution to finish
        final ExecutionStateResponseData[] executionState = {null};
        final CountDownLatch latch = new CountDownLatch(1);

        stateTimer = new Timer();
        TimerTask stateCheckTask = new TimerTask() {
            public void run() {
                try {
                    if (latch.getCount() == 0) {
                        cancel(); // Timeout reached, canceling timer
                        return;
                    }

                    LogHelper.Debug("Checking execution state...");
                    executionState[0] = checkExecutionState();

                    if (executionState[0] != null) {
                        if (executionState[0].hasFinished()) {
                            LogHelper.Info("Execution has finished - state: " + executionState[0].getState());
                            latch.countDown(); // Releasing the latch
                        } else {
                            LogHelper.Debug(executionState[0].getAgent() + " agent is still executing the job" + (executionState[0].getTarget() != null ? " on " + executionState[0].getTarget() : ""));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel();
                }
            }
        };

        stateTimer.scheduleAtFixedRate(stateCheckTask, 5000, 3000); // Will check for execution state every 3 seconds starting after 5 seconds
        boolean tpJobCompleted = latch.await(waitJobFinishSeconds, TimeUnit.SECONDS); // Waiting for timeout or the latch to reach 0
        stateTimer.cancel();
        LogHelper.Debug("Latch result: " + tpJobCompleted);

        if (!tpJobCompleted || executionState[0] == null || !executionState[0].hasFinished()) {
            throw new AbortException("The execution did not finish within the defined time frame");
        }

        if (!executionState[0].getReport().isEmpty()) {
            LogHelper.Info("Report: " + executionState[0].getReport());
        }

        if (executionState[0].hasFinishedWithErrors()) {
            String error = executionState[0].getMessage();

            throw new AbortException("The execution has finish with errors" + (error != null ? ": " + error : ""));
        }

        LogHelper.Info("The execution has finished successfully!");
    }

    private ExecutionStateResponseData checkExecutionState() throws IOException {
        ApiResponse<ExecutionStateResponseData> response = apiHelper.Get(String.format(Constants.TP_CHECK_EXECUTION_STATE_URL, projectId, jobId, executionId), ExecutionStateResponseData.class);

        if (response.isSuccessful()) {
            if (response.getData() != null) {
                return response.getData();
            }
        }

        throw new AbortException("Unable to get execution state!");
    }

    private void abortExecution() {

        if (aborting) // If already aborting
            return;

        aborting = true;
        LogHelper.Info("Aborting TestProject execution: " + executionId + "...");

        if (stateTimer != null) // Canceling the state stateTimer
            stateTimer.cancel();

        try {
            ApiResponse response = apiHelper.Post(String.format(Constants.TP_ABORT_EXECUTION_URL, projectId, jobId, executionId), Object.class);

            if (!response.isSuccessful()) {
                LogHelper.Info("Unable to abort TestProject job: " + response.getStatusCode());
            }

            LogHelper.Info("Aborted TestProject execution: " + executionId + "");

        } catch (IOException e) {
            LogHelper.Error(e);
        } finally {
            aborting = false;
        }
    }

    @Extension
    @Symbol(Constants.TP_JOB_SYMBOL)
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public static final int defaultWaitJobFinishSeconds = Constants.DEFAULT_WAIT_TIME;

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();

            return super.configure(req, formData);
        }

        @Override
        public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Constants.TP_JOB_DISPLAY_NAME;
        }

        public FormValidation doCheckProjectId(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("Project Id cannot be empty");

            return FormValidation.ok();
        }

        public FormValidation doCheckJobId(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("Job Id cannot be empty");

            return FormValidation.ok();
        }

        public FormValidation doCheckWaitJobFinishSeconds(@QueryParameter int value) {

            if (!(value == 0 || value >= 10))
                return FormValidation.error("Wait for job to finish must be at least 10 seconds (0 = Don't wait)");

            return FormValidation.ok();
        }

        public FormValidation doCheckExecutionParameters(@QueryParameter String value, @QueryParameter String agentId) {

            // Initial state where the user has just started to create the build step
            if (StringUtils.isEmpty(value) && StringUtils.isEmpty(agentId))
                return FormValidation.ok();

            JsonObject executionParams = null;
            try {
                executionParams = SerializationHelper.fromJson(value, JsonObject.class);

                // In case the value parameter is empty
                if (executionParams == null)
                    return FormValidation.ok();

            } catch (JsonSyntaxException e) {
                return FormValidation.error("Invalid JSON object");
            }

            // In case the user did not select agent from the dropdown and/or in the JSON object, return ok.
            if ((executionParams.get("agentId") != null && StringUtils.isEmpty(executionParams.get("agentId").getAsString())) || StringUtils.isEmpty(agentId))
                return FormValidation.ok();

            // In case the user has selected the same agentId in the dropdown and in the JSON object, no need to show a warning
            if ((executionParams.get("agentId") != null && StringUtils.equals(executionParams.get("agentId").getAsString(), agentId)))
                return FormValidation.ok();

            // In case the user has selected two different agents in the dropdown and in the JSON object, show warning
            if (executionParams.get("agentId") != null &&
                    !StringUtils.isEmpty(executionParams.get("agentId").getAsString()) &&
                    !StringUtils.isEmpty(agentId))
                return FormValidation.warning("You've selected an agent and specified a different one in executionParameters. The job will be executed on the selected agent.");

            return FormValidation.ok();
        }

        public ListBoxModel doFillProjectIdItems() {
            return DescriptorHelper.fillProjectIdItems();
        }

        public ListBoxModel doFillAgentIdItems() {
            HashMap<String, Object> headers = new HashMap<>();
            headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);

            ApiResponse<AgentData[]> response = null;
            try {
                ApiHelper apiHelper = new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey());
                response = apiHelper.Get(Constants.TP_RETURN_ACCOUNT_AGENTS, headers, AgentData[].class);

                if (!response.isSuccessful()) {
                    throw new AbortException(response.generateErrorMessage("Unable to fetch the agents list"));
                }

                ListBoxModel model = new ListBoxModel();
                model.add("Select an agent to override job default", "");
                for (AgentData agent : response.getData()) {
                    if (agent.getOsType().equals("Unknown"))
                        continue;

                    model.add(agent.getAlias() + " (v" + agent.getVersion() + " on " + agent.getOsType() + ") [" + agent.getId() + "]",
                            agent.getId());
                }

                return model;
            } catch (IOException | NullPointerException e) {
                LogHelper.Error(e);
            }

            return null;
        }

        public ListBoxModel doFillJobIdItems(@QueryParameter String projectId) {
            return DescriptorHelper.fillJobIdItems(projectId);
        }
    }
}
package io.testproject.plugins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import hudson.util.FormValidation;
import io.testproject.constants.Constants;
import io.testproject.helpers.ApiHelper;
import io.testproject.helpers.ApiResponse;
import io.testproject.helpers.LogHelper;
import io.testproject.model.ExecutionResponseData;
import io.testproject.model.ExecutionStateResponseData;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RunJob extends Builder implements SimpleBuildStep {

    private ApiHelper apiHelper;
    private String executionId;
    private Timer stateTimer;
    private boolean aborting;

    private @Nonnull
    String projectId;

    private @Nonnull
    String jobId;

    private int waitJobFinishSeconds;

    public RunJob() {
        this.projectId = "";
        this.jobId = "";

        this.waitJobFinishSeconds = 0;
    }

    @DataBoundConstructor
    public RunJob(@Nonnull String projectId, @Nonnull String jobId, int waitJobFinishSeconds) {
        this.projectId = projectId;
        this.jobId = jobId;
        this.waitJobFinishSeconds = waitJobFinishSeconds;
    }

    private void init() {
        if (apiHelper != null)
            return;

        LogHelper.Debug("Initializing API helper...");
        this.apiHelper = new ApiHelper(getDescriptor().getApiKey());
    }

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

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override // SimpleBuildStep implementation
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) {
        try {
            LogHelper.SetLogger(taskListener.getLogger(), getDescriptor().isVerbose());
            // logger which prints on job 'Console Output'
            LogHelper.Info("Sending a job run command to TestProject");
            triggerJob(run.getNumber());
        } catch (Exception e) {
            LogHelper.Error(e);
            run.setResult(Result.FAILURE);
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    private void triggerJob(Object buildNumber) throws Exception {

        try {
            init();

            LogHelper.Info(String.format("Starting TestProject job %s under project %s...", this.jobId, this.projectId));

            HashMap<String, Object> headers = new HashMap<>();
            headers.put(Constants.CI_NAME_HEADER, Constants.CI_NAME);
            headers.put(Constants.CI_BUILD_HEADER, buildNumber);

            ApiResponse<ExecutionResponseData> response = apiHelper.Post(String.format(Constants.TP_RUN_JOB_URL, projectId, jobId), headers, null, null, ExecutionResponseData.class);

            if (response.IsSuccessful()) {
                if (response.getData() != null) {
                    ExecutionResponseData data = response.getData();
                    executionId = data.getId();
                    LogHelper.Info("Execution id: " + executionId);

                    waitForJobFinish();
                }
            } else {
                int statusCode = response.getStatusCode();
                String responseMessage = response.getMessage();
                String message = "Unable to trigger TestProject job" + (statusCode > 0 ? " - " + statusCode : "") + (responseMessage != null ? " - " + responseMessage : "");

                throw new hudson.AbortException(message);
            }
        } catch (InterruptedException ie) {
            LogHelper.Error(ie);
            if (this.executionId != null) {
                abortExecution();
            }
        }
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
        latch.await(waitJobFinishSeconds, TimeUnit.SECONDS); // Waiting for timeout or the latch to reach 0
        latch.countDown(); // Setting count to 0
        stateTimer.cancel();

        if (executionState[0] == null || !executionState[0].hasFinished()) {
            throw new hudson.AbortException("The execution did not finish within the defined time frame");
        }

        if (!executionState[0].getReport().isEmpty()) {
            LogHelper.Info("Report: " + executionState[0].getReport());
        }

        if (executionState[0].hasFinishedWithErrors()) {
            String error = executionState[0].getMessage();

            throw new hudson.AbortException("The execution has finish with errors" + (error != null ? ": " + error : ""));
        }

        LogHelper.Info("The execution has finished successfully!");
    }

    private ExecutionStateResponseData checkExecutionState() throws IOException {
        ApiResponse<ExecutionStateResponseData> response = apiHelper.Get(String.format(Constants.TP_CHECK_EXECUTION_STATE_URL, projectId, jobId, executionId), ExecutionStateResponseData.class);

        if (response.IsSuccessful()) {
            if (response.getData() != null) {
                return response.getData();
            }
        }

        throw new hudson.AbortException("Unable to get execution state!");
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

            if (!response.IsSuccessful()) {
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

        private String apiKey;

        private boolean verbose;

        public static final int defaultWaitJobFinishSeconds = Constants.DEFAULT_WAIT_TIME;

        public DescriptorImpl() {
            load();
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public boolean isVerbose() {
            return verbose;
        }

        public void setVerbose(boolean verbose) {
            this.verbose = verbose;
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

        public FormValidation doCheckApiKey(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("Api Key cannot be empty");

            return FormValidation.ok();
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
    }
}

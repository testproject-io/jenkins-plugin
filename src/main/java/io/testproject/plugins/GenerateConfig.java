package io.testproject.plugins;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.*;
import hudson.util.ListBoxModel;
import io.testproject.constants.Constants;
import io.testproject.helpers.ApiHelper;
import io.testproject.helpers.ApiResponse;
import io.testproject.helpers.DescriptorHelper;
import io.testproject.helpers.LogHelper;
import io.testproject.model.AgentDockerConfigData;
import io.testproject.model.AgentDockerConfigGenerationRequestData;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class GenerateConfig extends Step {

    //region Private members
    private ApiHelper apiHelper;
    private String alias;
    private String projectId;
    private String jobId;
    private String jobParameters;
    //endregion

    //region Constructors
    @DataBoundConstructor
    public GenerateConfig(String alias, String projectId, String jobId, String jobParameters) {
        this.alias = alias;
        this.projectId = projectId;
        this.jobId = jobId;
        this.jobParameters = jobParameters;
    }

    public GenerateConfig() {
        this.alias = "";
        this.projectId = "";
        this.jobId = "";
        this.jobParameters = "";
    }
    //endregion

    //region Setters & Getters
    public String getProjectId() {
        return projectId;
    }

    @DataBoundSetter
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAlias() {
        return alias;
    }

    @DataBoundSetter
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getJobId() {
        return jobId;
    }

    @DataBoundSetter
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobParameters() {
        return jobParameters;
    }

    @DataBoundSetter
    public void setJobParameters(String jobParameters) {
        this.jobParameters = jobParameters;
    }

    public ApiHelper getApiHelper() {
        return apiHelper;
    }

    public void setApiHelper(ApiHelper apiHelper) {
        this.apiHelper = apiHelper;
    }
    //endregion

    @Override
    public StepExecution start(StepContext stepContext) {
        LogHelper.SetLogger(stepContext, PluginConfiguration.DESCRIPTOR.isVerbose());
        return new GenerateConfigExecution(this, stepContext);
    }

    public AgentDockerConfigData generateAgentConfigToken() throws IOException {
        LogHelper.Info("Sending a request to generate agent configuration token...");

        ApiResponse<AgentDockerConfigData> response = apiHelper.Post(
                Constants.TP_GENERATE_AGENT_CONFIG_TOKEN_URL,
                null,
                null,
                generateRequestBody(),
                AgentDockerConfigData.class);

        if (!response.isSuccessful()) {
            throw new AbortException(response.generateErrorMessage("Unable to generate agent configuration token"));
        }

        AgentDockerConfigData data = response.getData();

        if (data == null) {
            throw new AbortException(response.generateErrorMessage("Unable to generate agent configuration token"));
        }

        return data;
    }

    private AgentDockerConfigGenerationRequestData generateRequestBody() throws AbortException {
        JsonObject jobParams = null;

        try {
            jobParams = new Gson().fromJson(jobParameters, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new AbortException(e.getMessage());
        }

        // if the user provided browsers without job id, fail the step
        if (StringUtils.isEmpty(jobId) && jobParams != null)
            throw new AbortException("Cannot set job parameters without job id");

        // if the user did not provide an alias and jobId, send the body as null
        if (StringUtils.isEmpty(alias) && StringUtils.isEmpty(jobId))
            return null;

        AgentDockerConfigGenerationRequestData body = new AgentDockerConfigGenerationRequestData();

        if (!StringUtils.isEmpty(alias))
            body.setAlias(alias);

        if (!StringUtils.isEmpty(jobId)) {
            body.setJobId(jobId);

            if (jobParams != null)
                body.setJobParameters(jobParams);
        }
        return body;
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        public DescriptorImpl() {
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Constants.TP_GENERATE_AGENT_CONFIG_TOKEN;
        }

        @Override
        public String getFunctionName() {
            return Constants.TP_GENERATE_AGENT_CONFIG_TOKEN_SYMBOL;
        }

        public ListBoxModel doFillProjectIdItems() {
            return DescriptorHelper.fillProjectIdItems();
        }

        public ListBoxModel doFillJobIdItems(@QueryParameter String projectId) {
            return DescriptorHelper.fillJobIdItems(projectId);
        }
    }
}

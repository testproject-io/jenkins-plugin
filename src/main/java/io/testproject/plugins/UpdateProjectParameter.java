package io.testproject.plugins;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.testproject.constants.Constants;
import io.testproject.helpers.ApiHelper;
import io.testproject.helpers.ApiResponse;
import io.testproject.helpers.DescriptorHelper;
import io.testproject.helpers.LogHelper;
import io.testproject.model.ProjectParameterData;
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
import java.util.HashMap;

public class UpdateProjectParameter extends Builder implements SimpleBuildStep {

    //region Private members
    private ApiHelper apiHelper;

    private @Nonnull
    String projectId;

    private @Nonnull
    String parameterId;

    private @Nonnull
    String parameterValue;
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
    public String getParameterId() {
        return parameterId;
    }

    @DataBoundSetter
    public void setParameterId(@Nonnull String parameterId) {
        this.parameterId = parameterId;
    }

    @Nonnull
    public String getParameterValue() {
        return parameterValue;
    }

    @DataBoundSetter
    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }
    //endregion

    //region Constructors
    public UpdateProjectParameter() {
        this.projectId = "";
        this.parameterId = "";
        this.parameterValue = "";
    }

    @DataBoundConstructor
    public UpdateProjectParameter(@Nonnull String projectId, @Nonnull String parameterId, String parameterValue) {
        this.projectId = projectId;
        this.parameterId = parameterId;
        this.parameterValue = parameterValue;
    }
    //endregion

    private void init() {
        this.apiHelper = new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey());
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws AbortException {
        try {
            LogHelper.SetLogger(taskListener.getLogger(), PluginConfiguration.DESCRIPTOR.isVerbose());

            if (StringUtils.isEmpty(getProjectId()))
                throw new AbortException("The project id cannot be empty");

            if (StringUtils.isEmpty(getParameterId()))
                throw new AbortException("The project parameter id cannot be empty");

            if (StringUtils.isEmpty(getParameterValue()))
                throw new AbortException("The parameter value cannot be empty");

            init();
            updateProjectParameter();
        } catch (Exception e) {
            throw new AbortException(e.getMessage());
        }
    }

    private void updateProjectParameter() throws IOException {
        LogHelper.Info(String.format("Updating project parameter '%s' in project '%s' --> value: '%s'",
                getParameterId(), getProjectId(), getParameterValue()));

        ProjectParameterData body = new ProjectParameterData(getParameterValue());

        ApiResponse<ProjectParameterData> response = apiHelper.Put(
                String.format(Constants.TP_UPDATE_PROJECT_PARAMETERS, getProjectId(), getParameterId()),
                null,
                null,
                body,
                ProjectParameterData.class);

        if (!response.isSuccessful()) {
            throw new AbortException(response.generateErrorMessage("Unable to update the project parameter"));
        }

        LogHelper.Info(String.format("Successfully updated project parameter '%s' in project '%s' to value: '%s'",
                getParameterId(), getProjectId(), getParameterValue()));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    @Symbol(Constants.TP_PROJ_PARAM_SYMBOL)
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

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
            return Constants.TP_PROJ_PARAM_NAME;
        }

        public FormValidation doCheckProjectId(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("Project Id cannot be empty");

            return FormValidation.ok();
        }

        public FormValidation doCheckParameterId(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("Parameter Id cannot be empty");

            return FormValidation.ok();
        }

        public FormValidation doCheckParameterValue(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("Parameter name cannot be empty");

            return FormValidation.ok();
        }

        public ListBoxModel doFillProjectIdItems() {
            return DescriptorHelper.fillProjectIdItems();
        }

        public ListBoxModel doFillParameterIdItems(@QueryParameter String projectId) {
            if (projectId.isEmpty()) {
                return new ListBoxModel();
            }

            HashMap<String, Object> headers = new HashMap<>();
            headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);

            ApiResponse<ProjectParameterData[]> response = null;
            try {
                ApiHelper apiHelper = new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey());
                response = apiHelper.Get(String.format(Constants.TP_RETURN_PROJECT_PARAMETERS, projectId), headers, ProjectParameterData[].class);

                if (!response.isSuccessful()) {
                    throw new AbortException(response.generateErrorMessage("Unable to fetch the project parameters list"));
                }

                ListBoxModel model = new ListBoxModel();
                model.add("Select project parameter", "");
                for (ProjectParameterData parameter : response.getData()) {
                    model.add(
                            parameter.getName() + " [" + parameter.getId() + "]",
                            parameter.getId());
                }

                return model;
            } catch (IOException | NullPointerException e) {
                LogHelper.Error(e);
            }

            return null;
        }
    }
}

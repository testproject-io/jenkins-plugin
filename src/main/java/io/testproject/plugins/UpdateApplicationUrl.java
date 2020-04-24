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
import io.testproject.helpers.*;
import io.testproject.model.ApplicationData;
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

public class UpdateApplicationUrl extends Builder implements SimpleBuildStep {

    //region Private members
    private ApiHelper apiHelper;

    private @Nonnull
    String projectId;

    private @Nonnull
    String appId;

    private @Nonnull
    String applicationUrl;
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
    public String getAppId() {
        return appId;
    }

    @DataBoundSetter
    public void setAppId(@Nonnull String appId) {
        this.appId = appId;
    }

    @Nonnull
    public String getApplicationUrl() {
        return applicationUrl;
    }

    @DataBoundSetter
    public void setApplicationUrl(@Nonnull String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }
    //endregion

    //region Constructors
    public UpdateApplicationUrl() {
        this.projectId = "";
        this.appId = "";
        this.applicationUrl = "";
    }

    @DataBoundConstructor
    public UpdateApplicationUrl(@Nonnull String projectId, @Nonnull String appId, @Nonnull String applicationUrl) {
        this.projectId = projectId;
        this.appId = appId;
        this.applicationUrl = applicationUrl;
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
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
        try {
            LogHelper.SetLogger(taskListener.getLogger(), PluginConfiguration.DESCRIPTOR.isVerbose());

            if (StringUtils.isEmpty(getProjectId()))
                throw new AbortException("The project id cannot be empty");

            if (StringUtils.isEmpty(getAppId()))
                throw new AbortException("The application id cannot be empty");

            init();
            updateApplicationUrl();
        } catch (Exception e) {
            throw new AbortException(e.getMessage());
        }
    }

    private void updateApplicationUrl() throws IOException {
        LogHelper.Info(String.format("Updating application '%s' in project '%s' to URL '%s'",
                getAppId(), getProjectId(), getApplicationUrl()));

        ApplicationData body = new ApplicationData(getApplicationUrl());

        ApiResponse<ApplicationData> response = apiHelper.Put(
                String.format(Constants.TP_UPDATE_APP_URL, getProjectId(), getAppId()),
                null,
                null,
                body,
                ApplicationData.class);

        if (!response.isSuccessful()) {
            throw new AbortException(response.generateErrorMessage("Unable to update the project parameter"));
        }

        LogHelper.Info(String.format("Successfully updated the application '%s' in project '%s' to URL: '%s'",
                getAppId(), getProjectId(), getApplicationUrl()));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    @Symbol(Constants.TP_APP_URL_SYMBOL)
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
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Constants.TP_APP_URL_NAME;
        }

        public FormValidation doCheckProjectId(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("Project Id cannot be empty");

            return FormValidation.ok();
        }

        public FormValidation doCheckAppId(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("The application Id cannot be empty");

            return FormValidation.ok();
        }

        public FormValidation doCheckapplicationUrl(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("The application URL cannot be empty");

            return FormValidation.ok();
        }

        public ListBoxModel doFillProjectIdItems() {
            return DescriptorHelper.fillProjectIdItems();
        }

        public ListBoxModel doFillAppIdItems(@QueryParameter String projectId) {
            if (projectId.isEmpty()) {
                return new ListBoxModel();
            }

            HashMap<String, Object> headers = new HashMap<>();
            headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);

            ApiResponse<ApplicationData[]> response = null;
            try {
                ApiHelper apiHelper = new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey());
                response = apiHelper.Get(String.format(Constants.TP_RETURN_APP_FILE, projectId), headers, ApplicationData[].class);

                if (!response.isSuccessful()) {
                    throw new AbortException(response.generateErrorMessage("Unable to fetch the applications list"));
                }

                ListBoxModel model = new ListBoxModel();
                model.add("Select an application", "");
                for (ApplicationData application : response.getData()) {
                    if (application.getPlatform().equals("Web")) // only web applications
                        model.add(
                                application.getName() + " [" + application.getId() + "]",
                                application.getId());
                }

                return model;
            } catch (IOException | NullPointerException e) {
                LogHelper.Error(e);
            }

            return null;
        }
    }
}

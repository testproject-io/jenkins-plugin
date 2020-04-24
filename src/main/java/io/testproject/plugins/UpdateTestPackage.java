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
import io.testproject.model.TestPackageData;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateTestPackage extends Builder implements SimpleBuildStep {

    //region Private members
    private ApiHelper apiHelper;
    private boolean resolveConflicts;

    private @Nonnull
    String projectId;

    private @Nonnull
    String testPackageId;

    private @Nonnull
    String filePath;
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
    public String getTestPackageId() {
        return testPackageId;
    }

    @DataBoundSetter
    public void setTestPackageId(@Nonnull String testPackageId) {
        this.testPackageId = testPackageId;
    }

    @Nonnull
    public String getFilePath() {
        return filePath;
    }

    @DataBoundSetter
    public void setFilePath(@Nonnull String filePath) {
        this.filePath = filePath;
    }

    public boolean isResolveConflicts() {
        return resolveConflicts;
    }

    @DataBoundSetter
    public void setResolveConflicts(boolean resolveConflicts) {
        this.resolveConflicts = resolveConflicts;
    }
    //endregion

    //region Constructors
    public UpdateTestPackage() {
        this.projectId = "";
        this.testPackageId = "";
        this.filePath = "";
        this.resolveConflicts = false;
    }

    @DataBoundConstructor
    public UpdateTestPackage(@Nonnull String projectId, @Nonnull String testPackageId, @Nonnull String filePath, boolean resolveConflicts) {
        this.projectId = projectId;
        this.testPackageId = testPackageId;
        this.filePath = filePath;
        this.resolveConflicts = resolveConflicts;
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

            if (StringUtils.isEmpty(getTestPackageId()))
                throw new AbortException("The test package id cannot be empty");

            File file = new File(getFilePath());
            if (!file.exists() || !file.isFile())
                throw new AbortException(String.format("File '%s' does not exist", filePath));

            init();
            updateTestPackage();
        } catch (Exception e) {
            throw new AbortException(e.getMessage());
        }
    }

    private void updateTestPackage() throws IOException {
        LogHelper.Info(String.format("Updating test package '%s' in project '%s' with file '%s'",
                getTestPackageId(), getProjectId(), getFilePath()));

        HashMap<String, Object> headers = new HashMap<>();
        headers.put(Constants.CONTENT_TYPE, Constants.APPLICATION_OCTET_STREAM);

        Map<String, Object> queries = new HashMap<>();
        queries.put(Constants.RESOLVE_CONFLICTS, isResolveConflicts());

        ApiResponse<ProjectParameterData> response = apiHelper.Post(
                String.format(Constants.TP_UPDATE_TEST_PACKAGE, getProjectId(), getTestPackageId()),
                headers,
                queries,
                new File(getFilePath()),
                ProjectParameterData.class);

        if (response == null || !response.isSuccessful()) {
            throw new AbortException(response.generateErrorMessage("Unable to update the test package"));
        }

        LogHelper.Info(String.format("Successfully updated test package '%s' in project '%s' to file '%s'",
                getTestPackageId(), getProjectId(), getFilePath()));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    @Symbol(Constants.TP_TEST_PACKAGE_SYMBOL)
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() { load(); }

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
            return Constants.TP_TEST_PACKAGE_NAME;
        }

        public FormValidation doCheckProjectId(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("Project Id cannot be empty");

            return FormValidation.ok();
        }

        public FormValidation doCheckTestPackageId(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("The test package Id cannot be empty");

            return FormValidation.ok();
        }

        public FormValidation doCheckFilePath(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("The file path cannot be empty");

            return FormValidation.ok();
        }

        public ListBoxModel doFillProjectIdItems() {
            return DescriptorHelper.fillProjectIdItems();
        }

        public ListBoxModel doFillTestPackageIdItems(@QueryParameter String projectId) {
            if (projectId.isEmpty()) {
                return new ListBoxModel();
            }

            HashMap<String, Object> headers = new HashMap<>();
            headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);

            ApiResponse<TestPackageData[]> response = null;
            try {
                ApiHelper apiHelper = new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey());
                response = apiHelper.Get(String.format(Constants.TP_RETURN_TEST_PACKAGES, projectId), headers, TestPackageData[].class);

                if (!response.isSuccessful()) {
                    throw new AbortException(response.generateErrorMessage("Unable to fetch the test packages list"));
                }

                ListBoxModel model = new ListBoxModel();
                model.add("Select a test package", "");
                for (TestPackageData tp : response.getData()) {
                    model.add(
                            tp.getName() + " [" + tp.getId() + "]",
                            tp.getId());
                }

                return model;
            } catch (IOException | NullPointerException e) {
                LogHelper.Error(e);
            }

            return null;
        }
    }
}
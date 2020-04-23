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
import io.testproject.model.DataSourceData;
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

public class UpdateDataSourceFile extends Builder implements SimpleBuildStep {

    //region Private members
    private ApiHelper apiHelper;

    private @Nonnull
    String projectId;

    private @Nonnull
    String dataSourceId;

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
    public String getDataSourceId() {
        return dataSourceId;
    }

    @DataBoundSetter
    public void setDataSourceId(@Nonnull String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Nonnull
    public String getFilePath() {
        return filePath;
    }

    @DataBoundSetter
    public void setFilePath(@Nonnull String filePath) {
        this.filePath = filePath;
    }
    //endregion

    //region Constructors
    public UpdateDataSourceFile() {
        this.projectId = "";
        this.dataSourceId = "";
        this.filePath = "";
    }

    @DataBoundConstructor
    public UpdateDataSourceFile(@Nonnull String projectId, @Nonnull String dataSourceId, @Nonnull String filePath) {
        this.projectId = projectId;
        this.dataSourceId = dataSourceId;
        this.filePath = filePath;
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

            if (StringUtils.isEmpty(getDataSourceId()))
                throw new AbortException("The data source id cannot be empty");

            init();
            updateDataSourceFile();
        } catch (Exception e) {
            throw new AbortException(e.getMessage());
        }
    }

    private void updateDataSourceFile() throws IOException {
        // Creating instance of FileUploadHelper
        FileUploadHelper helper = new FileUploadHelper(apiHelper, projectId, dataSourceId, filePath, Constants.TP_DATA_SOURCE_SYMBOL);

        // Update the file in TestProject
        helper.updateFile();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    @Symbol(Constants.TP_DATA_SOURCE_SYMBOL)
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() { load(); }

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
            return Constants.TP_DATA_SOURCE_NAME;
        }

        public FormValidation doCheckProjectId(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("Project Id cannot be empty");

            return FormValidation.ok();
        }

        public FormValidation doCheckDataSourceId(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("The data source Id cannot be empty");

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

        public ListBoxModel doFillDataSourceIdItems(@QueryParameter String projectId) {
            if (projectId.isEmpty()) {
                return new ListBoxModel();
            }

            HashMap<String, Object> headers = new HashMap<>();
            headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);

            ApiResponse<DataSourceData[]> response = null;
            try {
                ApiHelper apiHelper = new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey());
                response = apiHelper.Get(
                        String.format(Constants.TP_RETURN_DATA_SOURCES, projectId),
                        headers,
                        DataSourceData[].class);

                if (!response.isSuccessful()) {
                    throw new AbortException(response.generateErrorMessage("Unable to fetch the data sources list"));
                }

                ListBoxModel model = new ListBoxModel();
                model.add("Select a data source", "");
                for (DataSourceData dataSource : response.getData()) {
                    model.add(
                            dataSource.getName() + " [" + dataSource.getGuid() + "]",
                            dataSource.getGuid());
                }

                return model;
            } catch (IOException | NullPointerException e) {
                LogHelper.Error(e);
            }

            return null;
        }
    }
}

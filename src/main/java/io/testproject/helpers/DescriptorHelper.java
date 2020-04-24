package io.testproject.helpers;

import hudson.AbortException;
import hudson.util.ListBoxModel;
import io.testproject.constants.Constants;
import io.testproject.model.JobData;
import io.testproject.model.ProjectData;
import io.testproject.plugins.PluginConfiguration;
import java.io.IOException;
import java.util.HashMap;

public class DescriptorHelper {

    public static ListBoxModel fillProjectIdItems() {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);

        ListBoxModel model = new ListBoxModel();

        ApiResponse<ProjectData[]> response = null;
        try {
            ApiHelper apiHelper = new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey());
            response = apiHelper.Get(Constants.TP_RETURN_ACCOUNT_PROJECTS, headers, ProjectData[].class);

            if (!response.isSuccessful()) {
                LogHelper.Debug(response.generateErrorMessage("Unable to fetch the projects list"));

                model.add("Invalid TestProject API key. Make sure you are using a valid API key in the global Jenkins configuration");
                return model;
            }

            model.add("Select a project", "");
            for (ProjectData project : response.getData()) {
                model.add(
                        project.getName() + " [" + project.getId() + "]",
                        project.getId());
            }

            return model;
        } catch (IOException | NullPointerException e) {
            LogHelper.Error(e);
        }

        return null;
    }

    public static ListBoxModel fillJobIdItems(String projectId) {
        if (projectId.isEmpty()) {
            return new ListBoxModel();
        }

        HashMap<String, Object> headers = new HashMap<>();
        headers.put(Constants.ACCEPT, Constants.APPLICATION_JSON);

        ApiResponse<JobData[]> response = null;
        try {
            ApiHelper apiHelper = new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey());
            response = apiHelper.Get(String.format(Constants.TP_RETURN_PROJECT_JOBS, projectId), headers, JobData[].class);

            if (!response.isSuccessful()) {
                String message = response.generateErrorMessage("Unable to fetch the project's jobs list");

                throw new AbortException(message);
            }

            ListBoxModel model = new ListBoxModel();
            model.add("Select a job to execute from the selected project (You must select a project first)", "");
            for (JobData job : response.getData()) {
                model.add(
                        job.getName() + " [" + job.getId() + "]",
                        job.getId());
            }

            return model;
        } catch (IOException | NullPointerException e) {
            LogHelper.Error(e);
        }

        return null;
    }
}

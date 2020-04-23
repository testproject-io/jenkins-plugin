package io.testproject.helpers;

import io.testproject.constants.Constants;
import io.testproject.model.FileNameData;
import io.testproject.model.UploadLinkData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FileUploadHelper {
    private ApiHelper apiHelper;
    private String projectId;
    private String artifactId;
    private String filePath;
    private String actionName;

    public FileUploadHelper(ApiHelper apiHelper, String projectId, String artifactId, String filePath, String actionName) {
        this.apiHelper = apiHelper;
        this.projectId = projectId;
        this.artifactId = artifactId;
        this.filePath = filePath;
        this.actionName = actionName;
    }

    public void updateFile() throws IOException {
        // Check if the path to the application exists
        File file = new File(filePath);
        if (!file.exists() || !file.isFile())
            throw new hudson.AbortException(String.format("File '%s' does not exist", filePath));

        // Sending a request to get an upload link
        String uploadLink = getUploadLink();

        // Uploading the file to S3
        uploadFile(uploadLink, file);

        // Confirm the new file upload
        confirmNewFile(file.getName());

        LogHelper.Info(String.format("Successfully updated the artifact '%s' in TestProject with file '%s'", artifactId, filePath));
    }

    private String getUploadLink() throws IOException {
        LogHelper.Info(String.format("Initializing TestProject artifact '%s' update in project '%s'", artifactId, projectId));

        String URL = actionName.equalsIgnoreCase(Constants.TP_APP_FILE_SYMBOL)
                ? Constants.TP_GET_UPLOAD_LINK_APP
                : Constants.TP_GET_UPLOAD_LINK_DS;

        ApiResponse<UploadLinkData> response = apiHelper.Get(
                String.format(URL, projectId, artifactId),
                UploadLinkData.class);

        if (response == null || !response.isSuccessful())
            throw new hudson.AbortException(String.format("Failed to initialize TestProject artifact '%s' update in project '%s'", artifactId, projectId));

        if (response.getData() != null) {
            return response.getData().getUrl();
        }

        return null;
    }

    private boolean uploadFile(String uploadLink, File file) throws IOException {
        LogHelper.Info(String.format("Uploading the artifact '%s' to TestProject", file.getPath()));

        HashMap<String, Object> headers = new HashMap<>();
        headers.put(Constants.CACHE_CONTROL_HEADER, Constants.NO_CACHE);
        headers.put(Constants.CONTENT_LENGTH, file.length());

        ApiResponse response = apiHelper.Put(
                uploadLink,
                headers,
                null,
                file,
                null);

        if (response == null || !response.isSuccessful())
            throw new hudson.AbortException(String.format("Failed to upload the artifact '%s' to TestProject", file.getPath()));

        return true;
    }

    private boolean confirmNewFile(String fileName) throws IOException {
        LogHelper.Info(String.format("Finalizing artifact '%s' update in TestProject", fileName));

        String URL = actionName.equalsIgnoreCase(Constants.TP_APP_FILE_SYMBOL)
                ? Constants.TP_CONFIRM_NEW_APP_FILE
                : Constants.TP_CONFIRM_NEW_DS_FILE;

        ApiResponse response = apiHelper.Post(
                String.format(URL, projectId, artifactId),
                null,
                null,
                new FileNameData(fileName),
                void.class);

        if (response == null || !response.isSuccessful())
            throw new hudson.AbortException(String.format("Failed to update artifact '%s' in TestProject", fileName));

        return true;
    }
}

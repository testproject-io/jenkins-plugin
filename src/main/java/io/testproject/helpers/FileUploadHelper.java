package io.testproject.helpers;

import hudson.AbortException;
import hudson.FilePath;
import io.testproject.constants.Constants;
import io.testproject.model.FileNameData;
import io.testproject.model.UploadLinkData;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FileUploadHelper {
    private final ApiHelper apiHelper;
    private final String projectId;
    private final String artifactId;
    private final String path;
    private final String actionName;
    private final FilePath filePath;
    private final ArrayList<String> validExtensions;

    public FileUploadHelper(
            ApiHelper apiHelper,
            String projectId,
            String artifactId,
            String path,
            String actionName,
            FilePath filePath,
            ArrayList<String> validExtensions) {
        this.apiHelper = apiHelper;
        this.projectId = projectId;
        this.artifactId = artifactId;
        this.path = path;
        this.actionName = actionName;
        this.filePath = filePath;
        this.validExtensions = validExtensions;
    }

    public FileUploadHelper() {
        this.apiHelper = null;
        this.projectId = "";
        this.artifactId = "";
        this.path = "";
        this.actionName = "";
        this.filePath = null;
        this.validExtensions = null;
    }

    public void updateFile() throws IOException, InterruptedException {
        // Validate the file path and generates a File object
        File file = getFileToUpload(path, filePath, validExtensions);

        // Sending a request to get an upload link
        String uploadLink = getUploadLink();

        // Uploading the file to S3
        uploadFile(uploadLink, file);

        // Confirm the new file upload
        confirmNewFile(file.getName());

        LogHelper.Info(String.format("Successfully updated the artifact '%s' in TestProject with file '%s'", artifactId, path));
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
            throw new AbortException(String.format("Failed to initialize TestProject artifact '%s' update in project '%s'", artifactId, projectId));

        if (response.getData() == null)
            throw new AbortException(String.format("Failed to initialize TestProject artifact '%s' update in project '%s'", artifactId, projectId));

        return response.getData().getUrl();
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
            throw new AbortException(String.format("Failed to upload the artifact '%s' to TestProject", file.getPath()));

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
            throw new AbortException(String.format("Failed to update artifact '%s' in TestProject", fileName));

        return true;
    }

    public File getFileToUpload(String sourceFile, FilePath filePath, ArrayList<String> validExtensions) throws IOException, InterruptedException {
        File file = new File(sourceFile);
        FilePath fp = new FilePath(filePath, file.getPath());
        String fileExtension = FilenameUtils.getExtension(file.getName());

        // Make sure that the file exists (relative/absolute path)
        if (!fp.exists())
            throw new hudson.AbortException(String.format("The file '%s' does not exist", file.getPath()));

        // If empty, there is no file in the provided path
        if (StringUtils.isEmpty(fileExtension))
            throw new hudson.AbortException("The file path does not contain any file");

        // Make sure that the user has provided a valid file extension
        if (!validExtensions.contains(fileExtension))
            throw new hudson.AbortException(String.format("Invalid file extension '%s'. Only '%s' formats are allowed.", fileExtension, Arrays.toString(validExtensions.toArray())));

        // Paths will throw IOExceptions if the path is not valid
        Paths.get(file.getPath());

        return new File(fp.getRemote());
    }
}

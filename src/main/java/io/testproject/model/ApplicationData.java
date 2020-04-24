package io.testproject.model;

/**
 * Representation of an application in the dropdown list of the build step
 */
public class ApplicationData {
    /**
     * The ID of the application
     */
    private String id;

    /**
     * The application's name
     */
    private String name;

    /**
     * Platform can be: Android, iOS or Web
     */
    private String platform;

    /**
     * The URL of the application in case of a Web application
     */
    private String url;

    /**
     * The name of the apk/ipa file
     */
    private String fileName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ApplicationData() {
    }

    public ApplicationData(String url) {
        this.url = url;
    }

    public ApplicationData(String id, String name, String platform, String url, String fileName) {
        this.id = id;
        this.name = name;
        this.platform = platform;
        this.url = url;
        this.fileName = fileName;
    }
}

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
     * The bundleId in case of an iOS application
     */
    private String appBundleId;

    /**
     * The URL of the application in case of a Web application
     */
    private String url;

    /**
     * The name of the apk/ipa file
     */
    private String fileName;

    /**
     * The size of the apk/ipa file
     */
    private long fileSize;

    /**
     * The activity name of the Android application (not apk file)
     */
    private String androidActivity;

    /**
     * The package name of the Android application (not apk file)
     */
    private String androidPackage;

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

    public String getAppBundleId() {
        return appBundleId;
    }

    public void setAppBundleId(String appBundleId) {
        this.appBundleId = appBundleId;
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getAndroidActivity() {
        return androidActivity;
    }

    public void setAndroidActivity(String androidActivity) {
        this.androidActivity = androidActivity;
    }

    public String getAndroidPackage() {
        return androidPackage;
    }

    public void setAndroidPackage(String androidPackage) {
        this.androidPackage = androidPackage;
    }

    public ApplicationData() {
    }

    public ApplicationData(String url) {
        this.url = url;
    }

    public ApplicationData(String id, String name, String platform, String appBundleId, String url, String fileName, long fileSize, String androidActivity, String androidPackage) {
        this.id = id;
        this.name = name;
        this.platform = platform;
        this.appBundleId = appBundleId;
        this.url = url;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.androidActivity = androidActivity;
        this.androidPackage = androidPackage;
    }
}

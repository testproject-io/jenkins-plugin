package io.testproject.model;

public class TestPackageData {
    /**
     * Test package id
     */
    private String id;

    /**
     * Test package name
     */
    private String name;

    /**
     * Test package description
     */
    private String description;

    /**
     * TestProject file model
     */
    private FileData file;

    /**
     * Test package's platform (web/Android/iOS/generic)
     */
    private String platform;

    /**
     * The language that the test was written with
     */
    private String language;

    /**
     * The last time that the test package was modified
     */
    private String lastModification;

    /**
     * Minimal supported SDK level
     */
    private int minSdkLevel;

    /**
     * Minimal supported SDK version
     */
    private String minSdkVersion;

    public TestPackageData() {
    }
    
    public TestPackageData(String id, String name, String description, FileData file, String platform, String language, String lastModification, int minSdkLevel, String minSdkVersion) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.file = file;
        this.platform = platform;
        this.language = language;
        this.lastModification = lastModification;
        this.minSdkLevel = minSdkLevel;
        this.minSdkVersion = minSdkVersion;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileData getFile() {
        return file;
    }

    public void setFile(FileData file) {
        this.file = file;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastModification() {
        return lastModification;
    }

    public void setLastModification(String lastModification) {
        this.lastModification = lastModification;
    }

    public int getMinSdkLevel() {
        return minSdkLevel;
    }

    public void setMinSdkLevel(int minSdkLevel) {
        this.minSdkLevel = minSdkLevel;
    }

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public void setMinSdkVersion(String minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }
}

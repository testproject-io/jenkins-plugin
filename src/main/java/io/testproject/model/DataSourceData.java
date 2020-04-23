package io.testproject.model;

/**
 * Representation of a data source in the dropdown list of the build step
 */
public class DataSourceData {
    /**
     * The id of the data source
     */
    private String id;
    /**
     * The guid of the data source
     */
    private String guid;
    /**
     * The id of the project that this data source belongs to
     */
    private long projectId;
    /**
     * The name of the data source
     */
    private String name;
    /**
     * The type of the data source (CSV, EXCEL,JSON,etc.)
     */
    private String type;
    /**
     * The name of the data source file
     */
    private String fileName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

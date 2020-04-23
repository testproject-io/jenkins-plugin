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
     * The description of the data source
     */
    private String description;
    /**
     * The creation date of the data source
     */
    private String creationDate;
    /**
     * The last modification date of the data source
     */
    private String modificationDate;
    /**
     * The type of the data source (CSV, EXCEL,JSON,etc.)
     */
    private String type;
    /**
     * The name of the data source file
     */
    private String fileName;
    /**
     * The size of the data source file
     */
    private int fileSize;
    /**
     * The hash of the data source file
     */
    private String fileHash;
    /**
     * The columns delimiter defined for the data source file
     */
    private String columnsDelimiter;
    /**
     * The rows delimiter defined for the data source file
     */
    private String rowsDelimiter;
    /**
     * The text qualifier of the data source file
     */
    private String textQualifier;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
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

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getColumnsDelimiter() {
        return columnsDelimiter;
    }

    public void setColumnsDelimiter(String columnsDelimiter) {
        this.columnsDelimiter = columnsDelimiter;
    }

    public String getRowsDelimiter() {
        return rowsDelimiter;
    }

    public void setRowsDelimiter(String rowsDelimiter) {
        this.rowsDelimiter = rowsDelimiter;
    }

    public String getTextQualifier() {
        return textQualifier;
    }

    public void setTextQualifier(String textQualifier) {
        this.textQualifier = textQualifier;
    }
}

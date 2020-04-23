package io.testproject.model;

/**
 * Project parameter data model
 */
public class ProjectParameterData {
    /**
     * Project parameter Id
     */
    private String id;

    /**
     * The name of the project parameter
     */
    private String name;

    /**
     * The description of the parameter
     */
    private String description;

    /**
     * The value of the project parameter
     */
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectParameterData(String name, String description, String value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public ProjectParameterData(String value) {
        this.value = value;
    }

    public ProjectParameterData() {
    }
}

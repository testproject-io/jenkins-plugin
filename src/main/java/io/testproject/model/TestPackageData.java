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

    public TestPackageData() {
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
}
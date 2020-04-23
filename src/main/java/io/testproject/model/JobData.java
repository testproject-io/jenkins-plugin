package io.testproject.model;

/**
 * Jobs response data
 */
public class JobData {
    /**
     * The job's Id
     */
    private String id;

    /**
     * The job's name
     */
    private String name;

    /**
     * The jobs description
     */
    private String description;

    /**
     * The target platform that the job is defined to
     */
    private String platform;

    /**
     * Is the job scheduled to run at future date?
     */
    private boolean isScheduled;

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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }
}

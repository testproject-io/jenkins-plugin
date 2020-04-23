package io.testproject.model;

import com.google.gson.JsonObject;

/**
 * Represents the request for generating an agent configuration token
 */
public class AgentDockerConfigGenerationRequestData {
    /**
     * The agent alias
     */
    private String alias;
    /**
     * The id of the job
     */
    private String jobId;
    /**
     * Json object of the job parameters
     */
    private JsonObject jobParameters;

    public AgentDockerConfigGenerationRequestData() {
    }

    public AgentDockerConfigGenerationRequestData(String alias, String jobId, JsonObject jobParameters) {
        this.alias = alias;
        this.jobId = jobId;
        this.jobParameters = jobParameters;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public JsonObject getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(JsonObject jobParameters) {
        this.jobParameters = jobParameters;
    }
}

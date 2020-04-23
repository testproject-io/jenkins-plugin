package io.testproject.model;

/**
 * Represents the response data of the generated agent configuration token
 */
public class AgentDockerConfigData {
    /**
     * Configuration (JWT)
     */
    private String config;
    /**
     * The guid of the new agent
     */
    private String agentGuid;

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getAgentGuid() {
        return agentGuid;
    }

    public void setAgentGuid(String agentGuid) {
        this.agentGuid = agentGuid;
    }
}

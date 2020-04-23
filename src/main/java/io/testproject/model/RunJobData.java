package io.testproject.model;

public class RunJobData {
    /**
     * The id of the agent
     */
    private String agentId;
    /**
     * Is queued?
     */
    private boolean queue;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public boolean isQueue() {
        return queue;
    }

    public void setQueue(boolean queue) {
        this.queue = queue;
    }

    public RunJobData(String agentId, boolean queue) {
        this.agentId = agentId;
        this.queue = queue;
    }

    public RunJobData() {
    }
}

package io.testproject.model;

import io.testproject.constants.AgentState;

/**
 * Agents response data
 */
public class AgentData {
    /**
     * Agent ID
     */
    private String id;

    /**
     * Agent Alias
     */
    private String alias;

    /**
     * Agent Version
     */
    private String version;

    /**
     * Name of the machine that the agent installed on
     */
    private String machineName;

    /**
     * The type of the machine's processor
     */
    private String processorType;

    /**
     * Amount of physical memory of the machine
     */
    private String physicalMemory;

    /**
     * Type of the operating system
     */
    private String osType;

    /**
     * The version of the OS
     */
    private String osVersion;

    /**
     * The status of the agent (private/public)
     */
    private String status;

    /**
     * The state of the agent (ENUM)
     */
    private AgentState state;

    /**
     * Last time that the connection with the agent occurred
     */
    private String lastHeartBeat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getProcessorType() {
        return processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }

    public String getPhysicalMemory() {
        return physicalMemory;
    }

    public void setPhysicalMemory(String physicalMemory) {
        this.physicalMemory = physicalMemory;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AgentState getState() {
        return state;
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    public String getLastHeartBeat() {
        return lastHeartBeat;
    }

    public void setLastHeartBeat(String lastHeartBeat) {
        this.lastHeartBeat = lastHeartBeat;
    }
}

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
     * Type of the operating system
     */
    private String osType;

    /**
     * The version of the OS
     */
    private String osVersion;

    public void setAlias(String alias) {
        this.alias = alias;
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

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public String getVersion() {
        return version;
    }

    public String getOsType() {
        return osType;
    }
}

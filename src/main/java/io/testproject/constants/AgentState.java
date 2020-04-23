package io.testproject.constants;

/**
 * Possible agent states
 */
public enum AgentState {
    /**
     * No state
     */
    None,
    /**
     * Disconnected
     */
    Disconnected,
    /**
     * Ready to use
     */
    Idle,
    /**
     * Executing test/job
     */
    Executing,
    /**
     * Used in development session
     */
    Dev,
    /**
     * Stopped
     */
    Stopped,
    /**
     * Updating the version
     */
    Updating,
    /**
     * During the registration phase
     */
    Registering,
    /**
     * Recording a test
     */
    Recording,
    /**
     * Uninstalling from the machine
     */
    Uninstalling,
    /**
     * Suspended
     */
    Suspended,
    /**
     * Busy and not available for recording/executing another test
     */
    Busy,
    /**
     * Launching the agent
     */
    Starting
}

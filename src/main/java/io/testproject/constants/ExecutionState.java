package io.testproject.constants;

/**
 * Possible execution states
 */
public enum ExecutionState {
    /**
     * Execution state is unknown
     */
    Unknown,
    /**
     * Ready for execution
     */
    Ready,
    /**
     * Execution skipped
     */
    Skipped,
    /**
     * Executed and passed
     */
    Passed,
    /**
     * Executed and failed
     */
    Failed,
    /**
     * Execution is aborting
     */
    Aborting,
    /**
     * Currently executing
     */
    Executing,
    /**
     * Done executing with errors
     */
    Error,
    /**
     * Execution was suspended
     */
    Suspended,
    /**
     * Agent is reporting execution results
     */
    Reporting
}

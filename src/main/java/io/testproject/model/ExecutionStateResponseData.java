package io.testproject.model;

import io.testproject.constants.ExecutionState;

/**
 * Execution state response
 */
public class ExecutionStateResponseData {

    /**
     * The current state of the execution
     */
    private ExecutionState state;

    /**
     * The Device/Browser that the job is running on
     */
    private String target;

    /**
     * The application that is running on the target device
     */
    private String app;

    /**
     * The agent that is executing
     */
    private String agent;

    /**
     * Execution message
     */
    private String message;

    /**
     * Report location
     */
    private String report;

    public ExecutionState getState() {
        return state;
    }

    public void setState(ExecutionState state) {
        this.state = state;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public boolean hasFinished() {
        return state == ExecutionState.Passed
                || state == ExecutionState.Failed
                || state == ExecutionState.Skipped
                || state == ExecutionState.Error;
    }

    public boolean hasFinishedSuccessfully() {
        return state == ExecutionState.Passed;
    }

    public boolean hasFinishedWithErrors() {
        return state == ExecutionState.Error
                || state == ExecutionState.Failed;
    }
}

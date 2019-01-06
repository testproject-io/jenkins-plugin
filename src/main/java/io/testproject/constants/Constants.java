package io.testproject.constants;

/**
 * Project constants
 */
public class Constants {
    private static final String TP_BASE_URL = "https://api.testproject.io";

    public static final String TP_RUN_JOB_URL = TP_BASE_URL + "/v2/projects/%s/jobs/%s/run";
    public static final String TP_CHECK_EXECUTION_STATE_URL = TP_BASE_URL + "/v2/projects/%s/jobs/%s/executions/%s/state";
    public static final String TP_ABORT_EXECUTION_URL = TP_BASE_URL + "/v2/projects/%s/jobs/%s/executions/%s/abort";

    public static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    public static final int DEFAULT_READ_TIMEOUT = 30000;

    public static final int DEFAULT_WAIT_TIME = 30; // Seconds

    public static final String AUTH_HEADER = "Authorization";

    public static final String TP_JOB_SYMBOL = "runtpjob";
    public static final String TP_JOB_DISPLAY_NAME = "Run TestProject Job";

    public static final String CI_NAME = "Jenkins";
    public static final String CI_NAME_HEADER = "CI-Name";
    public static final String CI_BUILD_HEADER = "CI-Build";
}

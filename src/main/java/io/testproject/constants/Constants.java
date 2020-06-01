package io.testproject.constants;

/**
 * Project constants
 */
public class Constants {
    private static final String TP_BASE_URL = "https://api.testproject.io";

    public static final String TP_RUN_JOB_URL = TP_BASE_URL + "/v2/projects/%s/jobs/%s/run";
    public static final String TP_CHECK_EXECUTION_STATE_URL = TP_BASE_URL + "/v2/projects/%s/jobs/%s/executions/%s/state";
    public static final String TP_ABORT_EXECUTION_URL = TP_BASE_URL + "/v2/projects/%s/jobs/%s/executions/%s/abort";
    public static final String TP_RETURN_ACCOUNT_AGENTS = TP_BASE_URL + "/v2/agents";
    public static final String TP_RETURN_ACCOUNT_PROJECTS = TP_BASE_URL + "/v2/projects";
    public static final String TP_RETURN_PROJECT_JOBS = TP_BASE_URL + "/v2/projects/%s/jobs";
    public static final String TP_RETURN_PROJECT_PARAMETERS = TP_BASE_URL + "/v2/projects/%s/parameters";
    public static final String TP_UPDATE_PROJECT_PARAMETERS = TP_BASE_URL + "/v2/projects/%s/parameters/%s";
    public static final String TP_RETURN_TEST_PACKAGES = TP_BASE_URL + "/v2/projects/%s/test-packages";
    public static final String TP_UPDATE_TEST_PACKAGE = TP_BASE_URL + "/v2/projects/%s/test-packages/%s";
    public static final String TP_RETURN_APP_FILE = TP_BASE_URL + "/v2/projects/%s/applications";
    public static final String TP_GET_UPLOAD_LINK_APP = TP_BASE_URL + "/v2/projects/%s/applications/%s/file/upload-link";
    public static final String TP_GET_UPLOAD_LINK_DS = TP_BASE_URL + "/v2/projects/%s/data-sources/%s/file/upload-link";
    public static final String TP_CONFIRM_NEW_APP_FILE = TP_BASE_URL + "/v2/projects/%s/applications/%s/file";
    public static final String TP_CONFIRM_NEW_DS_FILE = TP_BASE_URL + "/v2/projects/%s/data-sources/%s/file";
    public static final String TP_RETURN_DATA_SOURCES = TP_BASE_URL + "/v2/projects/%s/data-sources";
    public static final String TP_GENERATE_AGENT_CONFIG_TOKEN_URL = TP_BASE_URL + "/v2/agents/config";
    public static final String TP_UPDATE_APP_URL = TP_BASE_URL + "/v2/projects/%s/applications/%s";
    public static final String TP_GET_JUNIT_XML_REPORT = TP_BASE_URL + "/v2/projects/%s/jobs/%s/reports/%s";

    public static final int DEFAULT_CONNECT_TIMEOUT = 90000;
    public static final int DEFAULT_READ_TIMEOUT = 90000;

    public static final int DEFAULT_WAIT_TIME = 30; // Seconds

    public static final String AUTH_HEADER = "Authorization";
    public static final String ACCEPT = "accept";

    public static final String TP_JOB_SYMBOL = "tpJobRun";
    public static final String TP_PROJ_PARAM_SYMBOL = "tpProjectParamUpdate";
    public static final String TP_TEST_PACKAGE_SYMBOL = "tpTestPackageUpdate";
    public static final String TP_APP_FILE_SYMBOL = "tpAppUpdateFile";
    public static final String TP_APP_URL_SYMBOL = "tpAppUpdateURL";
    public static final String TP_DATA_SOURCE_SYMBOL = "tpDataSourceUpdate";
    public static final String TP_GENERATE_AGENT_CONFIG_TOKEN_SYMBOL = "tpAgentConfig";

    public static final String TP_JOB_DISPLAY_NAME = "Run TestProject Job";
    public static final String TP_PROJ_PARAM_NAME = "Update TestProject Project Parameter";
    public static final String TP_TEST_PACKAGE_NAME = "Update TestProject Test Package";
    public static final String TP_APP_FILE_NAME = "Update TestProject Mobile Application";
    public static final String TP_APP_URL_NAME = "Update TestProject Web Application";
    public static final String TP_DATA_SOURCE_NAME = "Update TestProject Data Source";
    public static final String TP_GENERATE_AGENT_CONFIG_TOKEN = "Generate TestProject Agent Configuration";
    public static final String TP_PLUGIN_CONFIGURATION = "TestProject Global Configuration";

    public static final String CI_NAME = "Jenkins";
    public static final String CI_NAME_HEADER = "CI-Name";
    public static final String CI_BUILD_HEADER = "CI-Build";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String APPLICATION_XML = "application/xml";
    public static final String CACHE_CONTROL_HEADER = "cache-control";
    public static final String NO_CACHE = "no-cache";
    public static final String CONTENT_LENGTH = "Content-length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String RESOLVE_CONFLICTS = "resolveConflicts";
    public static final String DETAILS = "details";
    public static final String FORMAT = "format";
    public static final String FILE_NAME = "fileName";

    public static final String JUNIT_FILE_PREFIX = "JUnitReport_";
    public static final String FORMAT_JUNIT = "JUnit";
}

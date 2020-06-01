# TestProject plugin for Jenkins

[![](https://storage-static.testproject.io/jenkins/tp-jenkins-banner.jpg)]()

[TestProject](https://testproject.io) plugin for Jenkins provides an easy way to execute TestProject jobs, update applications, data sources, project parameter, test packages and generate configuration for the TestProject Agent.

[![Version](https://img.shields.io/jenkins/plugin/v/testproject?color=%2307003c&style=for-the-badge)](https://plugins.jenkins.io/testproject/)
[![Installs](https://img.shields.io/jenkins/plugin/i/testproject?style=for-the-badge)](https://plugins.jenkins.io/testproject/)
[![License](https://img.shields.io/github/license/jenkinsci/testproject-plugin?style=for-the-badge)](https://github.com/jenkinsci/testproject-plugin/blob/master/LICENSE.md)

# Prerequisites

In order to use this plugin you need to have:
1. An active TestProject account which can be created for free at https://testproject.io.
2. At least one registered and running TestProject Agent.

# How to use

* Install the plugin.
* Go to project Configure > TestProject.
* Set your TestProject API Key (which can be obtained [here](https://app.testproject.io/#/developers/api)).

> This plugin supports both FreeStyle and Pipeline jobs.

# Build Steps

## Running a TestProject Job
Using this step, you can trigger TestProject jobs as part of your Jenkins build.
To trigger a job, you need to provide the following parameters:
* `projectId` - The ID of the project containing the job.
* `jobId` - The ID of the job to execute.
* `agentId` _(optional)_ - The ID of the TestProject agent that will execute the job. Leave this field empty to use the default agent defined for this job.
* `waitJobFinishSeconds` - How many seconds should the step wait for the automation job to finish. If **0** is provided, the setup will not wait for the job to finish execution.
* `junitResultsFile` _(optional)_ - Path (including the file name) to a file where the JUnit XML report will be stored. The file path can be absolute or relative to your workspace.
* `executionParameters` _(optional)_ - A JSON object that allows you to override the job's default settings and parameters for a single execution. Here's an example:

```JSON
{
  "browsers": [
    "Chrome"
  ],
  "devices": [
    "AAA111BBB"
  ],
  "queue": true,
  "restartDriver": true,
  "projectParameters": {
    "ProjectParameter1": "Value1",
    "ProjectParameter2": "Value2",
    "ProjectParameter3": "Value3"
  },
  "testParameters": [
    {
      "testId": "string",
      "testPosition": 0,
      "dataSourceId": "string",
      "reinstallApp": true,
      "data": [
        {
          "TestParameter1": "Value1",
          "TestParameter2": "Value2",
          "TestParameter3": "Value3"
        }
      ]
    }
  ]
}
```
**Please visit [our API documentation](https://api.testproject.io/docs/v2/#/Jobs/Jobs_RunJobAsync) to read more about using execution parameters when running a job.**

### Free Style syntax
 
> ![Copy ID](https://storage-static.testproject.io/jenkins/run-job-with-params.png)

### Pipeline syntax

```groovy
tpJobRun projectId: '<PROJECT_ID>', jobId: '<JOB_ID>', agentId: '<AGENT_ID>', waitJobFinishSeconds: 180, junitResultsFile: '<JUNIT_RESULTS_FILE>', executionParameters: '<EXECUTION_PARAMETERS>'
```

## Updating a Mobile Application (apk/ipa) File
Using this step, you can update an existing Android or iOS application file as part of your build.<br>
The step accepts the following parameters:
* `projectId` - The ID of the project in containing the application.
* `applicationId` - The ID of the application to update.
* `filePath` - The path to `apk/ipa` file. The file path can be absolute or relative to your workspace.

### Free Style syntax
 
> ![Copy ID](https://storage-static.testproject.io/jenkins/update-mobile-app.png)

### Pipeline syntax

```groovy
tpAppUpdateFile appId: '<APP_ID>', filePath: '<FILE_PATH>', projectId: '<PROJECT_ID>'
```

## Updating a Web Application URL
Using this step, you can update the URL of a web application.<br>
The step accepts the following parameters:
* `projectId` - The ID of the project containing the application.
* `applicationId` - The ID of the application to update.
* `applicationUrl` - The new URL address.

### Free Style syntax

> ![Copy ID](https://storage-static.testproject.io/jenkins/update-web-app.png)

### Pipeline syntax

```groovy
tpAppUpdateURL appId: '<APP_ID>', applicationUrl: '<APP_URL>', projectId: '<PROJECT_ID>'
```

## Updating a Data Source
Using this step, you can update an existing data source file (`.csv`).<br>
This step accepts the following parameters:
* `projectId` - The ID of the project containing the data source.
* `dataSourceId` - The ID of the data source to update.
* `filePath` - The path to the data source (`.csv`) file. The file path can be absolute or relative to your workspace.

### Free Style syntax
  
> ![Copy ID](https://storage-static.testproject.io/jenkins/update-data-source.png)

### Pipeline syntax

```groovy
tpDataSourceUpdate dataSourceId: '<DATA_SOURCE_ID>', filePath: '<FILE_PATH>', projectId: '<PROJECT_ID>'
```

## Updating a Project Parameter
Using this step, you can update the value of any project parameter in your project.<br>
The step accepts the following parameters:
* `projectId` - The ID of the project containing the parameter.
* `parameterId` - The ID of the parameter to update.
* `value` - The new value that should be assigned to the parameter.

### Free Style syntax

> ![Copy ID](https://storage-static.testproject.io/jenkins/update-project-parameter.png)

### Pipeline syntax

```groovy
tpParamProjectUpdate parameterId: '<PARAMETER_ID>', parameterValue: '<PARAMETER_VALUE>', projectId: '<PROJECT_ID>'
```

## Updating a Test Package
Using this step, you can update an existing test package (coded test) in your project.<br>
The step accepts the following parameters:
* `projectId` - The ID of the project containing the test package.
* `testPackageId` - The ID of the test package to update.
* `filePath` - The path to the new test package file (`.jar`/`.dll`/`.zip`). The file path can be absolute or relative to your workspace.
* `resolveConflicts` [true/false] - Should TestProject try to automatically resolve conflicts.<br>
  A conflict may arise if the updated test package is used by other tests or the new packages contains breaking changes such as removed test cases, etc.

### Free Style syntax

> ![Copy ID](https://storage-static.testproject.io/jenkins/update-test-package.png)

### Pipeline syntax

```groovy
tpTestPackageUpdate filePath: '<FILE_PATH>', projectId: '<PROJECT_ID>', resolveConflicts: '<TRUE/FALSE>', testPackageId: '<TEST_PACKAGE_ID>'
```

## Generating TestProject Agent Configuration
Using this step, you can generate configuration for a TestProject Agent.<br>
This can be used to allow a TestProject Agent running in a docker container to automatically register, execute a job and terminate on completion.<br>

This step accepts the following parameters:
* `agentAlias` - An alias (name) for the agent.<br>
  > This parameter is optional. If no value is provided, TestProject will generate an alias for you.
* `projectId` - The ID of the project containing the job to execute.
* `jobId` - The ID of the job to execute.
* `jobParameters` - A JSON object containing job execution parameters.
  > This parameter is optional. If no value is provided, the job will be executed with its default configuration.<br>
  
  Example:
  ```json
  {
      "browsers": [
          "ChromeHeadless",
          "FirefoxHeadless"
      ]
  }
  ```

**Please visit [our docker hub page](https://hub.docker.com/r/testproject/agent) to read more about TestProject Agents containers**

### Pipeline syntax

```groovy
tpAgentConfig alias: '<AGENT_ALIAS>', jobId: '<JOB_ID>', jobParameters: '<JOB_PARAMETERS>', projectId: '<PROJECT_ID>'
```

To read the agent configuration object in your pipeline script and access to the `config` property, follow the example below:
```groovy
node {
    def agentConfig = tpAgentConfig(
            alias: '<AGENT_ALIAS>', 
            jobId: '<JOB_ID>', 
            jobParameters: '<JOB_PARAMETERS>', 
            projectId: '<PROJECT_ID>')

    print(agentConfig.config)
}
```

# Additional info

WebSite: https://testproject.io

Blog: https://blog.testproject.io

Forum: https://forum.testproject.io

Addons: https://addons.testproject.io

Docker Hub: https://hub.docker.com/r/testproject/agent

YouTube: https://www.youtube.com/channel/UCEAPPxNvHT74Xj6Ixt28mNw

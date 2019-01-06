# TestProject plugin for Jenkins

[TestProject](https://testproject.io) plugin provides an easy way to execute TestProject jobs.

> This plugin supports both FreeStyle and Pipeline jobs

## Prerequisites

In order to use this plugin you need to:
1. Have an active TestProject account which can be obtained for free at the [TestProject website](https://testproject.io).
2. At least one registered and running TestProject Agent.
3. A job (in your account) containing the tests that you wish to run.

## How to use

* Install the plugin.
* Go to project Configure > TestProject.
* Provide your TestProject API Key which can be obtained [here](https://app.testproject.io/#/developers/api).

### Parameters

In addition to the API key that needs to be provided once in global configuration when executing a job you will have to 
provide 3 additional parameters:
* projectId - The ID of the project that contains the job you with to run.
* jobId - The ID of the job that you with to run.
* waitJobFinishSeconds - How many seconds should Jenkins build wait for the automation job to finish. If **0** is provided, 
Jenkins will not wait for TestProject to finish execution.   

> To get Project/Job IDs, use the context menu in [TestProject application](https://app.testproject.io).  
> ![Copy ID](https://storage-static.testproject.io/jenkins/copy-id.png)

### Pipeline syntax

```
    runtpjob jobId: '<JOB_ID>', projectId: '<PROJECT_ID>', waitJobFinishSeconds: 180
```

## Additional info

YouTube tutorial: https://www.youtube.com/playlist?list=PL6tu16kXT9PrUJ842VaGcSNqIN7THFUlN  

TestProject Blog: https://blog.testproject.io

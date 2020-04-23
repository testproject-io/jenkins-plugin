package io.testproject.plugins;

import hudson.model.Result;
import io.testproject.helpers.ApiHelper;
import io.testproject.helpers.LogHelper;
import io.testproject.model.AgentDockerConfigData;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import javax.annotation.Nonnull;

public class GenerateConfigExecution extends SynchronousNonBlockingStepExecution<AgentDockerConfigData> {
    private transient GenerateConfig step;

    protected GenerateConfigExecution(@Nonnull GenerateConfig step, @Nonnull StepContext context) {
        super(context);
        this.step = step;
    }

    @Override
    protected AgentDockerConfigData run() {
        try {
            init();
            return step.generateAgentConfigToken();
        } catch (Exception e) {
            LogHelper.Error(e);
            getContext().setResult(Result.FAILURE);
        }

        return null;
    }

    private void init() {
        step.setApiHelper(new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey()));
    }
}

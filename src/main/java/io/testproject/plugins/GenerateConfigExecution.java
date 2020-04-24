package io.testproject.plugins;

import hudson.AbortException;
import hudson.model.Result;
import io.testproject.helpers.ApiHelper;
import io.testproject.helpers.LogHelper;
import io.testproject.model.AgentDockerConfigData;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import javax.annotation.Nonnull;

public class GenerateConfigExecution extends SynchronousNonBlockingStepExecution<AgentDockerConfigData> {
    private final transient GenerateConfig step;
    private static final long serialVersionUID = 1L;

    protected GenerateConfigExecution(@Nonnull GenerateConfig step, @Nonnull StepContext context) {
        super(context);
        this.step = step;
    }

    @Override
    protected AgentDockerConfigData run() throws AbortException {
        try {
            init();
            return step.generateAgentConfigToken();
        } catch (Exception e) {
            throw new AbortException(e.getMessage());
        }
    }

    private void init() {
        step.setApiHelper(new ApiHelper(PluginConfiguration.DESCRIPTOR.getApiKey()));
    }
}

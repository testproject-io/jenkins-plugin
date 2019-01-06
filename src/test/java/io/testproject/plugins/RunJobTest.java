package io.testproject.plugins;

import hudson.model.*;
import hudson.tasks.*;
import org.junit.*;
import org.jvnet.hudson.test.*;

import javax.annotation.Nonnull;
import java.io.PrintStream;


public class RunJobTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @ClassRule
    public static BuildWatcher bw = new BuildWatcher();

    private static BuildListener buildListener = new BuildListener() {
        @Nonnull
        @Override
        public PrintStream getLogger() {
            return System.out;
        }
    };

    @Test
    public void triggerJob() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();

        Builder step = new RunJob("PROJECT-ID", "JOB-ID", 15000);
        ((RunJob.DescriptorImpl)step.getDescriptor()).setApiKey("API-KEY");

        project.getBuildersList().add(step);

        // Enqueue a build of the project, wait for it to complete, and assert success
        FreeStyleBuild build = j.buildAndAssertSuccess(project);
    }
}
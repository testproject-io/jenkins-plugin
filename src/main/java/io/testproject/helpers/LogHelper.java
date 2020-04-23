package io.testproject.helpers;

import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import java.io.PrintStream;

public class LogHelper {
    private static PrintStream logger;

    private static boolean verbose;

    public static void SetLogger(PrintStream logger, boolean verbose) {
        LogHelper.logger = logger;
        LogHelper.verbose = verbose;
    }

    public static void SetLogger(StepContext context, boolean verbose) {
        LogHelper.logger = getTaskListener(context).getLogger();
        LogHelper.verbose = verbose;
    }

    public static void Info(String message) {
        printMessage(message);
    }

    public static void Error(Exception e) {
        String err = e.getMessage() != null
                ? e.getMessage()
                : "An unknown error occurred";

        printMessage(String.format("Error: %s", err));
    }

    public static void Debug(String message) {
        if (!verbose)
            return;

        printMessage(message);
    }

    private static void printMessage(String message) {
        if (logger == null)
            return;

        logger.println(message);
    }

    private static TaskListener getTaskListener(StepContext context) {
        if (!context.isReady()) {
            return null;
        }
        try {
            return context.get(TaskListener.class);
        } catch (Exception x) {
            return null;
        }
    }
}

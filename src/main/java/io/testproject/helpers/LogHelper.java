package io.testproject.helpers;

import java.io.PrintStream;

public class LogHelper {
    private static PrintStream logger;

    private static boolean verbose;

    public static void SetLogger(PrintStream logger, boolean verbose) {
        LogHelper.logger = logger;
        LogHelper.verbose = verbose;
    }

    public static void Info(String message) {
        printMessage(message);
    }

    public static void Error(Exception e) {
        printMessage(e.getMessage());
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
}

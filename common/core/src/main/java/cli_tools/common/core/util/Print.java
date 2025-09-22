package cli_tools.common.core.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.Ansi;

public class Print {
    public static void print(String text, Object... args) {
        System.out.printf(text + "%n", args);
    }

    public static void print() {
        System.out.println();
    }

    public static void printError(String text, Object... args) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(text.formatted(args)).reset());
    }

    public static void logException(Exception e, Logger log) {
        if (e.getMessage() != null) {
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        } else {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public static void printAndLogException(Exception e, Logger log, Level level) {
        if (e.getMessage() != null) {
            Print.printError(e.getMessage());
            log.log(level, "{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        } else {
            Print.printError(ExceptionUtils.getStackTrace(e));
            log.log(level, ExceptionUtils.getStackTrace(e));
        }
    }

    public static void printAndLogException(Exception e, Logger log) {
        printAndLogException(e, log, Level.ERROR);
    }

    public static void logException(Exception e, Logger log, Level level) {
        if (e.getMessage() != null) {
            log.log(level, "{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        } else {
            log.log(level, ExceptionUtils.getStackTrace(e));
        }
    }

    public static void printInfo(String text, Object... args) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a(text.formatted(args)).reset());
    }

    public static void printWarning(String text, Object... args) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(text.formatted(args)).reset());
    }
}

package cli_tools.common.core.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.Ansi;

public class Print {
    public static void print(String text, Object ...args) {
        System.out.printf(text + "%n", args);
    }

    public static void printError(String text, Object ...args) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(text.formatted(args)).reset());
    }

    public static void printAndLogException(Exception e, Logger log) {
        if (e.getMessage() != null) {
            Print.printError(ExceptionUtils.getStackTrace(e));
            log.error(ExceptionUtils.getStackTrace(e));
        } else {
            Print.printError(e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    public static void printInfo(String text, Object ...args) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a(text.formatted(args)).reset());
    }

    public static void printWarning(String text, Object ...args) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(text.formatted(args)).reset());
    }
}

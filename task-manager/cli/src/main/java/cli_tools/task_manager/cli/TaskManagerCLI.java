package cli_tools.task_manager.cli;

import java.io.IOException;
import java.util.*;

import cli_tools.task_manager.cli.command_line.CommandLine;
import cli_tools.task_manager.cli.command_line.Executor;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Log4j2
public class TaskManagerCLI {

    public static void main(String @NonNull [] args) {
        Injector injector = Guice.createInjector(new AppModule());

        try {
            if (args.length >= 1) {
                Executor executor = injector.getInstance(Executor.class);
                executor.execute(getTokenList(args));
            } else {
                CommandLine commandLine = injector.getInstance(CommandLine.class);
                commandLine.run();
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private static @NonNull List<String> getTokenList(String[] args) {
        List<String> argList = new ArrayList<>();

        for (String arg : args) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < arg.length(); ) {
                if (arg.charAt(j) == '\\' && j < arg.length() - 1) {
                    sb.append(arg.charAt(j + 1));
                    j += 2;
                } else {
                    sb.append(arg.charAt(j));
                    j += 1;
                }
            }
            argList.add(sb.toString());
        }

        return argList;
    }

}

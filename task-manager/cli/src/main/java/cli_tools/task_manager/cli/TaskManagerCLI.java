package cli_tools.task_manager.cli;

import java.io.IOException;

import cli_tools.common.cli.command_line.CommandLine;
import cli_tools.task_manager.cli.init.Initializer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Log4j2
public class TaskManagerCLI {

    public static void main(String @NonNull [] args) {
        Injector injector = Guice.createInjector(new TaskManagerModule());
        Initializer initializer = injector.getInstance(Initializer.class);

        try {
            if (initializer.needsInitialization()) {
                initializer.initialize();
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));        }

        CommandLine commandLine = injector.getInstance(CommandLine.class);

        try {
            commandLine.run();
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}

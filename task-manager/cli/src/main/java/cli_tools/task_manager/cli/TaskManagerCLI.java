package cli_tools.task_manager.cli;

import cli_tools.common.cli.command.custom_command.CustomCommandDefinition;
import cli_tools.common.cli.command.custom_command.CustomCommandParserFactory;
import cli_tools.common.cli.command.custom_command.repository.CustomCommandRepository;
import cli_tools.common.cli.command_line.CommandLine;
import cli_tools.common.cli.command_parser.CommandParserFactory;
import cli_tools.common.core.util.Print;
import cli_tools.task_manager.cli.command_parser.*;
import cli_tools.task_manager.cli.init.Initializer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

@Log4j2
public class TaskManagerCLI {

    public static void main(String @NonNull [] args) {
        Injector injector = Guice.createInjector(new TaskManagerModule());

        Initializer initializer = injector.getInstance(Initializer.class);
        CommandParserFactory commandParserFactory = injector.getInstance(CommandParserFactory.class);
        CustomCommandRepository customCommandRepository = injector.getInstance(CustomCommandRepository.class);
        CustomCommandParserFactory customCommandParserFactory = injector.getInstance(CustomCommandParserFactory.class);
        CommandLine commandLine = injector.getInstance(CommandLine.class);

        try {
            if (initializer.needsInitialization()) {
                initializer.initialize();
            }
        } catch (IOException e) {
            Print.printError(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
            return;
        }

        commandParserFactory.registerParser("add", AddTaskCommandParser::new);
        commandParserFactory.registerParser("list", ListTasksCommandParser::new);
        commandParserFactory.registerParser("done", DoneTaskCommandParser::new);
        commandParserFactory.registerParser("undone", UndoneTaskCommandParser::new);
        commandParserFactory.registerParser("clear", ClearCommandParser::new);
        commandParserFactory.registerParser("delete", DeleteTaskCommandParser::new);
        commandParserFactory.registerParser("modify", ModifyTaskCommandParser::new);
        commandParserFactory.registerParser("ai", AICommandParser::new);

        try {
            for (CustomCommandDefinition customCommandDefinition : customCommandRepository.getAll()) {
                commandParserFactory.registerParser(customCommandDefinition.getCommandName(),
                        () -> customCommandParserFactory.createParser(customCommandDefinition));
            }
        } catch (IOException e) {
            Print.printError(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }

        try {
            commandLine.run();
        } catch (IOException e) {
            Print.printError(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}

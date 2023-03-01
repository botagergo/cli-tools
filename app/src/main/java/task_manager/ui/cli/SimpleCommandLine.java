package task_manager.ui.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import task_manager.api.command.Command;
import task_manager.ui.cli.command_parser.ArgumentList;
import task_manager.ui.cli.command_parser.CommandParser;
import task_manager.ui.cli.command_parser.CommandParserFactory;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;

public class SimpleCommandLine implements CommandLine {

    public SimpleCommandLine() {
        input = System.in;
        output = System.out;
        scanner = new Scanner(input);

        argParser = new RegexArgumentParser();

        commandParserFactory = new CommandParserFactoryImpl();
        executor = new Executor(input, output);
    }

    public void run() {
        output.print(prompt);
        while (scanner.hasNext()) {
            String commandStr = scanner.nextLine().trim();

            try {
                ArgumentList arguments = argParser.parse(commandStr);

                if (arguments.commandName == null) {
                    continue;
                }

                CommandParser parser = commandParserFactory.getParser(arguments);
                Command command = parser.parse(arguments);
                executor.execute(command);

                if (executor.shouldExit()) {
                    break;
                }
            } catch (Exception e) {
                output.println(e);
                e.printStackTrace();
            }

            output.print(prompt);
        }
    }

    private InputStream input;
    private PrintStream output;

    private Scanner scanner;

    private ArgumentParser argParser;
    private CommandParserFactory commandParserFactory;
    private Executor executor;

    private static String prompt = "> ";
}

package task_manager.ui.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader.Option;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import task_manager.api.command.Command;
import task_manager.ui.cli.command_parser.ArgumentList;
import task_manager.ui.cli.command_parser.CommandParser;
import task_manager.ui.cli.command_parser.CommandParserFactory;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;

public class JlineCommandLine implements CommandLine {

    public JlineCommandLine() {
        input = System.in;
        output = System.out;
        commandParserFactory = new CommandParserFactoryImpl();
        executor = new Executor(input, output);
    }

    @Override
    public void run() throws IOException {
        Terminal terminal = TerminalBuilder.terminal();
        LineReader reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .option(Option.DISABLE_EVENT_EXPANSION, true)
            .build();

        String line;
        while ((line = reader.readLine(prompt)) != null) {
            List<String> arguments = reader.getParser().parse(line, 0).words();

            if (arguments.size() == 0) {
                continue;
            }

            ArgumentList argList = new ArgumentList(
                arguments.get(0),
                arguments.subList(1, arguments.size()),
                List.of());

            CommandParser parser = commandParserFactory.getParser(argList);
            if (parser == null) {
                output.println("Unknown command: " + argList.commandName);
                continue;
            }

            Command command = parser.parse(argList);
            executor.execute(command);

            if (executor.shouldExit()) {
                break;
            }
        }
    }

    private InputStream input;
    private PrintStream output;

    private CommandParserFactory commandParserFactory;
    private Executor executor;

    private static String prompt = "> ";

}

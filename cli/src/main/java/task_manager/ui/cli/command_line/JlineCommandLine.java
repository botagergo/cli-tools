package task_manager.ui.cli.command_line;

import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader.Option;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import task_manager.init.Initializer;
import task_manager.ui.cli.AppModule;
import task_manager.ui.cli.Executor;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command_parser.CommandParser;
import task_manager.ui.cli.command_parser.CommandParserException;
import task_manager.ui.cli.command_parser.CommandParserFactory;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;
import task_manager.ui.cli.tokenizer.MismatchedQuotesException;
import task_manager.ui.cli.tokenizer.TokenList;
import task_manager.ui.cli.tokenizer.Tokenizer;

public class JlineCommandLine implements CommandLine {

    public JlineCommandLine() {
        commandParserFactory = new CommandParserFactoryImpl();
    }

    @Override
    public void run() throws IOException {
        Injector injector = Guice.createInjector(new AppModule());

        Initializer initializer = injector.getInstance(Initializer.class);
        if (initializer.needsInitialization()) {
            initializer.initialize();
        }

        Executor executor = injector.getInstance(Executor.class);

        Tokenizer tokenizer = injector.getInstance(Tokenizer.class);

        Terminal terminal = TerminalBuilder.terminal();
        LineReader reader = LineReaderBuilder.builder().terminal(terminal)
            .option(Option.DISABLE_EVENT_EXPANSION, true).build();

        String line;
        String prompt = "> ";
        while ((line = reader.readLine(prompt)) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            TokenList tokenList;
            try {
                tokenList = tokenizer.tokenize(line);
                if (tokenList.tokens().size() == 0) {
                    continue;
                }
            } catch (MismatchedQuotesException e) {
                System.out.println("Syntax error: mismatched quotes");
                continue;
            }

            ArgumentList argList = ArgumentList.from(tokenList);
            if (argList.getCommandName().equals("exit")) {
                break;
            }

            CommandParser parser = commandParserFactory.getParser(argList);
            if (parser == null) {
                System.out.println("Unknown command: " + argList.getCommandName());
                continue;
            }

            try {
                Command command = parser.parse(argList);
                executor.execute(command);
            } catch (CommandParserException e) {
                System.out.println("Error parsing command: " + e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private final CommandParserFactory commandParserFactory;

}

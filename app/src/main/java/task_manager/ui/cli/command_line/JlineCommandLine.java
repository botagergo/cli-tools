package task_manager.ui.cli.command_line;

import java.io.IOException;
import java.util.List;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader.Option;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import task_manager.AppModule;
import task_manager.api.Executor;
import task_manager.api.command.Command;
import task_manager.api.use_case.PropertyDescriptorUseCase;
import task_manager.db.property.PropertyDescriptorCollection;
import task_manager.db.property.PropertyManager;
import task_manager.db.task.Task;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command_parser.CommandParser;
import task_manager.ui.cli.command_parser.CommandParserFactory;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;

public class JlineCommandLine implements CommandLine {

    public JlineCommandLine() {
        commandParserFactory = new CommandParserFactoryImpl();
    }

    @Override
    public void run() throws IOException {
        Injector injector = Guice.createInjector(new AppModule());
        executor = injector.getInstance(Executor.class);

        PropertyDescriptorUseCase propertyDescriptorUseCase =
            injector.getInstance(PropertyDescriptorUseCase.class);
        PropertyDescriptorCollection propertyDescriptors =
            propertyDescriptorUseCase.getPropertyDescriptors();

        Task.setPropertyManager(new PropertyManager(propertyDescriptors));

        Terminal terminal = TerminalBuilder.terminal();
        LineReader reader = LineReaderBuilder.builder().terminal(terminal)
                .option(Option.DISABLE_EVENT_EXPANSION, true).build();

        String line;
        while ((line = reader.readLine(prompt)) != null) {
            List<String> args = reader.getParser().parse(line, 0).words();

            if (args.size() == 0) {
                continue;
            }

            ArgumentList argList = ArgumentList.from(args);

            CommandParser parser = commandParserFactory.getParser(argList);
            if (parser == null) {
                System.out.println("Unknown command: " + argList.commandName);
                continue;
            }

            Command command = parser.parse(argList);
            executor.execute(command);

            if (executor.shouldExit()) {
                break;
            }
        }
    }

    private CommandParserFactory commandParserFactory;
    private Executor executor;

    private static String prompt = "> ";

}

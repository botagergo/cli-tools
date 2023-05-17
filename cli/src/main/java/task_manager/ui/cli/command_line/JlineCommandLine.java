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
import task_manager.data.Task;
import task_manager.data.property.PropertyDescriptorCollection;
import task_manager.data.property.PropertyManager;
import task_manager.init.Initializer;
import task_manager.logic.use_case.PropertyDescriptorUseCase;
import task_manager.ui.cli.AppModule;
import task_manager.ui.cli.Executor;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
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

        Initializer initializer = injector.getInstance(Initializer.class);
        if (initializer.needsInitialization()) {
            initializer.initialize();
        }

        PropertyDescriptorUseCase propertyDescriptorUseCase =
            injector.getInstance(PropertyDescriptorUseCase.class);
        PropertyDescriptorCollection propertyDescriptors =
            propertyDescriptorUseCase.getPropertyDescriptors();

        Task.setPropertyManager(new PropertyManager(propertyDescriptors));

        Executor executor = injector.getInstance(Executor.class);

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

            List<String> args = reader.getParser().parse(line, 0).words();
            if (args.size() == 0) {
                continue;
            }

            ArgumentList argList = ArgumentList.from(args);
            if (argList.commandName.equals("exit")) {
                break;
            }

            CommandParser parser = commandParserFactory.getParser(argList);
            if (parser == null) {
                System.out.println("Unknown command: " + argList.commandName);
                continue;
            }

            Command command = parser.parse(argList);
            try {
                executor.execute(command);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private final CommandParserFactory commandParserFactory;

}

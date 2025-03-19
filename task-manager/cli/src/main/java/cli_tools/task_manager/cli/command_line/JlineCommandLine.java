package cli_tools.task_manager.cli.command_line;

import cli_tools.task_manager.init.Initializer;
import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import org.jline.reader.*;
import org.jline.reader.LineReader.Option;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyDescriptorCollection;
import cli_tools.task_manager.cli.Context;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class JlineCommandLine implements CommandLine {

    @Override
    public void run() throws IOException {
        if (initializer.needsInitialization()) {
            initializer.initialize();
        }

        Context context = ((ExecutorImpl) executor).getContext();
        List<PropertyDescriptor> propertyDescriptors = context.getPropertyDescriptorService().getPropertyDescriptors();
        context.getPropertyManager().setPropertyDescriptorCollection(PropertyDescriptorCollection.fromList(propertyDescriptors));

        Terminal terminal = TerminalBuilder.builder()
                .nativeSignals(true)
                .signalHandler(Terminal.SignalHandler.SIG_IGN)
                .build();

        cli_tools.task_manager.cli.command_line.Completer completer = buildCompleter(context);
        LineReader reader = LineReaderBuilder.builder().terminal(terminal)
                .completer(completer)
                .parser(new Parser())
                .completionMatcher(new CompletionMatcher())
                .option(Option.RECOGNIZE_EXACT, true)
                .option(Option.GROUP_PERSIST, true)
                .option(Option.DISABLE_EVENT_EXPANSION, true).build();

        String line;
        String prompt = "> ";
        while (true) {
            try {
                line = reader.readLine(prompt).trim();
                if (line.isEmpty()) {
                    continue;
                }
                executor.execute(line);
                if (executor.shouldExit()) {
                    break;
                }
            } catch (UserInterruptException ignored) {

            }
        }
    }

    private cli_tools.task_manager.cli.command_line.Completer buildCompleter(Context context) {
        return new Completer(
                context,
                List.of("add", "list", "modify", "delete", "clear", "done"));
    }

    private final Initializer initializer;
    private final Executor executor;

}

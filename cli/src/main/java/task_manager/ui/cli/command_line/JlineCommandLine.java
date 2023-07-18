package task_manager.ui.cli.command_line;

import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import task_manager.init.Initializer;
import task_manager.logic.pseudo_property_provider.TaskIDPseudoPropertyProvider;
import task_manager.ui.cli.Context;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class JlineCommandLine implements CommandLine {

    @Override
    public void run() throws IOException {
        if (initializer.needsInitialization()) {
            initializer.initialize();
        }

        Context context = ((ExecutorImpl) executor).getContext();
        context.getPropertyManager()
                .registerPseudoPropertyProvider("id", new TaskIDPseudoPropertyProvider(context.getTempIDMappingUseCase()));

        Terminal terminal = TerminalBuilder.builder()
                .nativeSignals(true)
                .signalHandler(Terminal.SignalHandler.SIG_IGN)
                .build();

        LineReader reader = LineReaderBuilder.builder().terminal(terminal)
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

    private final Initializer initializer;
    private final Executor executor;

}

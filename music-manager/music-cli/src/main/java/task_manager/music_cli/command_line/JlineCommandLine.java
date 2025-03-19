package common.music_cli.command_line;

import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import common.music_logic.pseudo_property_provider.TaskIDPseudoPropertyProvider;
import common.property_lib.PropertyDescriptor;
import common.property_lib.PropertyDescriptorCollection;
import common.music_cli.Context;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class JlineCommandLine implements CommandLine {

    @Override
    public void run() throws IOException {
        /*if (initializer.needsInitialization()) {
            initializer.initialize();
        }*/

        Context context = ((ExecutorImpl) executor).getContext();

        List<PropertyDescriptor> propertyDescriptors = context.getPropertyDescriptorUseCase().getPropertyDescriptors();
        context.getPropertyManager().setPropertyDescriptorCollection(PropertyDescriptorCollection.fromList(propertyDescriptors));

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

    private final Executor executor;

}

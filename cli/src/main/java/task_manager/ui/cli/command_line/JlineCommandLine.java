package task_manager.ui.cli.command_line;

import java.io.IOException;

import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader.Option;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import task_manager.init.Initializer;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class JlineCommandLine implements CommandLine {

    @Override
    public void run() throws IOException {
        if (initializer.needsInitialization()) {
            initializer.initialize();
        }

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
            executor.execute(line);
        }
    }

    private final Initializer initializer;
    private final Executor executor;

}

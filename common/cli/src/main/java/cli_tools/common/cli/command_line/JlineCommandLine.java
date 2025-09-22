package cli_tools.common.cli.command_line;

import cli_tools.common.cli.executor.Executor;
import lombok.AllArgsConstructor;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;

import static cli_tools.common.cli.Util.strip;

@AllArgsConstructor
public class JlineCommandLine implements CommandLine {

    private final Executor executor;
    private final Completer completer;
    private final File historyFile;

    @Override
    public void run() throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .nativeSignals(true)
                .signalHandler(Terminal.SignalHandler.SIG_IGN)
                .build();

        Parser parser = new cli_tools.common.cli.command_line.Parser();
        LineReader reader = LineReaderBuilder.builder().terminal(terminal)
                .completer(completer)
                .parser(parser)
                .completionMatcher(new cli_tools.common.cli.command_line.CompletionMatcher())
                .option(Option.RECOGNIZE_EXACT, true)
                .option(Option.GROUP_PERSIST, true)
                .option(Option.DISABLE_EVENT_EXPANSION, true)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "> ")
                .variable(LineReader.INDENTATION, 2)
                .variable(LineReader.HISTORY_FILE, historyFile)
                .build();

        String line;
        String prompt = "$ ";
        while (true) {
            try {
                line = strip(reader.readLine(prompt));

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

}

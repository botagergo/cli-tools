package task_manager.ui.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

public class Executor {
    public Executor(InputStream input, PrintStream output) {
        this.input = input;
        this.output = output;
    }

    public void execute(List<String> commands) {
        output.println("You've executed \"" + String.join(" ", commands) + "\"");
    }

    public boolean shouldExit() {
        return false;
    }

    private InputStream input;
    private PrintStream output;
}

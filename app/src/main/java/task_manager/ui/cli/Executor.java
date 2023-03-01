package task_manager.ui.cli;

import java.io.InputStream;
import java.io.PrintStream;

import task_manager.api.command.Command;

public class Executor {
    public Executor(InputStream input, PrintStream output) {
        this.input = input;
        this.output = output;
    }

    public void execute(Command command) {
        command.execute();
    }

    public boolean shouldExit() {
        return false;
    }

    private InputStream input;
    private PrintStream output;
}

package task_manager;

import java.io.IOException;
import task_manager.ui.cli.command_line.CommandLine;
import task_manager.ui.cli.command_line.JlineCommandLine;

public class App {

    public static void main(String[] args) throws IOException {
        CommandLine commandLine = new JlineCommandLine();
        commandLine.run();
    }
}

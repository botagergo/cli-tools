package task_manager;

import java.io.IOException;

import task_manager.ui.cli.*;

public class App {

    public static void main(String[] args) throws IOException {
        CommandLine commandLine = new JlineCommandLine();
        commandLine.run();
    }
}
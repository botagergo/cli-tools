package task_manager;

import task_manager.ui.cli.*;

public class App {

    public static void main(String[] args) {
        CommandLine commandLine = new SimpleCommandLine();
        commandLine.run();
    }
}
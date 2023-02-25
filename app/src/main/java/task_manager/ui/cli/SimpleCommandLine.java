package task_manager.ui.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class SimpleCommandLine implements CommandLine {

    public SimpleCommandLine() {
        input = System.in;
        output = System.out;
        scanner = new Scanner(input);

        argParser = new RegexArgumentParser();
        executor = new Executor(input, output);
    }

    public void run() {
        output.print(prompt);
        while (scanner.hasNext()) {
            String command = scanner.nextLine().trim();

            try {
                List<String> arguments = argParser.parse(command);
                executor.execute(arguments);

                if (executor.shouldExit()) {
                    break;
                }
            } catch (ArgumentParserException e) {
                output.println(e.getMessage());
            }

            output.print(prompt);
        }
    }

    private InputStream input;
    private PrintStream output;

    private Scanner scanner;

    private ArgumentParser argParser;
    private Executor executor;

    private static String prompt = "> ";
}

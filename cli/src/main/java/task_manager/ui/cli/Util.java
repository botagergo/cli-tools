package task_manager.ui.cli;

import task_manager.ui.cli.command_parser.CommandParserException;

import java.util.Scanner;

public class Util {
    public static boolean yesNo(String prompt) {
        Scanner scanner = new Scanner(System.in);

        System.out.print(prompt + " (y/n)");
        String answer = scanner.nextLine();

        return answer.equalsIgnoreCase("y");
    }

    public static int parseTaskID(String str) throws CommandParserException {
        try {
            int taskID = Integer.parseInt(str);
            if (taskID < 1) {
                throw new CommandParserException("Invalid id: " + str);
            }
            return taskID;
        } catch (NumberFormatException e) {
            throw new CommandParserException("Invalid id: " + str);
        }
    }
}

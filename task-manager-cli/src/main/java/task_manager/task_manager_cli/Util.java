package task_manager.task_manager_cli;

import task_manager.task_manager_cli.command_parser.CommandParserException;

public class Util {
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

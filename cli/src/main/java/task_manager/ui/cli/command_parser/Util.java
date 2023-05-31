package task_manager.ui.cli.command_parser;

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

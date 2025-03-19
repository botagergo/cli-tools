package common.music_cli;

import common.music_cli.command_parser.CommandParserException;

public class Util {

    public static int parseID(String str) throws CommandParserException {
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

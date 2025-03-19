package cli_tools.task_manager.cli.command_parser;

import cli_tools.task_manager.cli.Util;
import lombok.NonNull;
import cli_tools.task_manager.cli.Context;

import java.util.ArrayList;
import java.util.List;

public class ParseUtil {
    public static List<@NonNull Integer> getTaskIDs(@NonNull Context context, List<String> taskIDArguments) throws CommandParserException {
        if (taskIDArguments == null || taskIDArguments.isEmpty()) {
            return null;
        }

        List<Integer> taskIDs = new ArrayList<>();
        for (String arg : taskIDArguments) {
            if (arg.equals("~")) {
                if (context.getPrevTaskID() == null) {
                    throw new CommandParserException("No previous task id exists");
                }
                taskIDs.add(context.getPrevTaskID());
            } else {
                taskIDs.add(Util.parseTaskID(arg));
            }
        }
        return taskIDs;
    }
}

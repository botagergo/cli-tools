package task_manager.task_manager_cli.command_parser;

import lombok.NonNull;
import task_manager.task_manager_cli.Context;
import task_manager.task_manager_cli.Util;

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

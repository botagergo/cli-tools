package task_manager.music_cli.command_parser;

import lombok.NonNull;
import task_manager.music_cli.Context;
import task_manager.music_cli.Util;

import java.util.ArrayList;
import java.util.List;

public class ParseUtil {
    public static List<@NonNull Integer> getIDs(@NonNull Context context, List<String> idArguments) throws CommandParserException {
        if (idArguments == null || idArguments.isEmpty()) {
            return null;
        }

        List<Integer> ids = new ArrayList<>();
        for (String arg : idArguments) {
            if (arg.equals("~")) {
                if (context.getGetPrevID() == null) {
                    throw new CommandParserException("No previous id exists");
                }
                ids.add(context.getGetPrevID());
            } else {
                ids.add(Util.parseID(arg));
            }
        }
        return ids;
    }
}

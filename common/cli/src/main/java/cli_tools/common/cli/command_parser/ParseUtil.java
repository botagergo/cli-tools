package cli_tools.common.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.Util;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ParseUtil {
    public static List<@NonNull Integer> getTempIds(@NonNull Context context, List<String> tempIdArgs) throws CommandParserException {
        List<Integer> tempIds = new ArrayList<>();

        if (tempIdArgs == null || tempIdArgs.isEmpty()) {
            return tempIds;
        }

        for (String arg : tempIdArgs) {
            if (arg.equals("~")) {
                if (context.getPrevTempId() == null) {
                    throw new CommandParserException("No previous temp id exists");
                }
                tempIds.add(context.getPrevTempId());
            } else {
                tempIds.add(Util.parseTempId(arg));
            }
        }
        return tempIds;
    }
}

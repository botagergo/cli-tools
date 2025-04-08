package cli_tools.common.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.command.Command;

import java.util.List;

public abstract class CommandParser {
    public abstract Command parse(Context context, ArgumentList argList) throws CommandParserException;


    protected String parseSingleOptionValue(String optionArgumentName, List<String> values) throws CommandParserException {
        if (values == null || values.isEmpty()) {
            throw new CommandParserException("option '" + optionArgumentName + "' needs an argument");
        } else if (values.size() != 1) {
            throw new CommandParserException("option '" + optionArgumentName + "' accepts one argument");
        }

        return values.get(0);
    }

    protected List<String> parseListOptionValue(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values;
    }
}

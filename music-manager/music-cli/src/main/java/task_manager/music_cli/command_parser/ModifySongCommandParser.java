package common.music_cli.command_parser;

import common.cli.argument.ArgumentList;
import common.music_cli.Context;
import common.music_cli.command.Command;
import common.music_cli.command.ModifySongCommand;

import java.util.List;

public class ModifySongCommandParser implements CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        if (!argList.getTrailingPositionalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected trailing arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        List<Integer> taskIDs = ParseUtil.getIDs(context, argList.getLeadingPositionalArguments());
        return new ModifySongCommand(taskIDs, argList.getFilterPropertyArguments(), argList.getModifyPropertyArguments());
    }

}

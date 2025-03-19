package common.music_cli.command_parser;

import common.cli.argument.ArgumentList;
import common.music_cli.Context;
import common.music_cli.command.Command;
import common.music_cli.command.DeleteSongCommand;

import java.util.List;

public class DeleteSongCommandParser implements CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("Unexpected property arguments");
        } else if (!argList.getTrailingNormalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected trailing arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        List<Integer> ids = ParseUtil.getIDs(context, argList.getLeadingNormalArguments());
        return new DeleteSongCommand(ids, argList.getFilterPropertyArguments());
    }

}

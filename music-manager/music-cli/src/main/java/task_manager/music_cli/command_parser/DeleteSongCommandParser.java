package task_manager.music_cli.command_parser;

import task_manager.cli_lib.argument.ArgumentList;
import task_manager.music_cli.Context;
import task_manager.music_cli.command.Command;
import task_manager.music_cli.command.DeleteSongCommand;

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

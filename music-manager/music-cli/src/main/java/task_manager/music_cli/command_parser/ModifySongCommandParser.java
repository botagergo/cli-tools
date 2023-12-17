package task_manager.music_cli.command_parser;

import task_manager.cli_lib.argument.ArgumentList;
import task_manager.music_cli.Context;
import task_manager.music_cli.command.Command;
import task_manager.music_cli.command.ModifySongCommand;

import java.util.List;

public class ModifySongCommandParser implements CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        if (!argList.getTrailingNormalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected trailing arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        List<Integer> taskIDs = ParseUtil.getIDs(context, argList.getLeadingNormalArguments());
        return new ModifySongCommand(taskIDs, argList.getFilterPropertyArguments(), argList.getModifyPropertyArguments());
    }

}

package common.music_cli.command_parser;

import lombok.NonNull;
import common.cli.argument.ArgumentList;
import common.music_cli.Context;
import common.music_cli.command.AddSongCommand;
import common.music_cli.command.Command;

public class AddSongCommandParser implements CommandParser {

    @Override
    public Command parse(@NonNull Context context, ArgumentList argList) throws CommandParserException {
        if (!argList.getFilterPropertyArguments().isEmpty()) {
            throw new CommandParserException("Unexpected filter arguments");
        } else if (!argList.getLeadingPositionalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected leading arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        return new AddSongCommand(
                String.join(" ", argList.getTrailingPositionalArguments()),
                argList.getModifyPropertyArguments());
    }

}

package common.music_cli.command_parser;

import common.cli.argument.ArgumentList;

public class CommandParserFactoryImpl implements CommandParserFactory {

    @Override
    public CommandParser getParser(ArgumentList argList) {
        return switch (argList.getCommandName()) {
            case "add" -> new AddSongCommandParser();
            case "list" -> new ListSongsCommandParser();
            case "clear" -> new ClearCommandParser();
            case "delete" -> new DeleteSongCommandParser();
            case "modify" -> new ModifySongCommandParser();
            default -> null;
        };
    }

}

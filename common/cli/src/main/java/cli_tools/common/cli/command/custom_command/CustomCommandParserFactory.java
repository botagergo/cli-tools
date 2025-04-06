package cli_tools.common.cli.command.custom_command;

public interface CustomCommandParserFactory {
     CustomCommandParser createParser(CustomCommandDefinition customCommandDefinition);
}

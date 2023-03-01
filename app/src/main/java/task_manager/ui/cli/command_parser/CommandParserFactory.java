package task_manager.ui.cli.command_parser;

public interface CommandParserFactory {
    public CommandParser getParser(ArgumentList argList) throws UnknownCommandException, NullCommandException;
}

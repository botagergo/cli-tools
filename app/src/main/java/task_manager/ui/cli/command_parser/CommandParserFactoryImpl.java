package task_manager.ui.cli.command_parser;

public class CommandParserFactoryImpl implements CommandParserFactory {

    @Override
    public CommandParser getParser(ArgumentList argList)
            throws UnknownCommandException, NullCommandException {
        if (argList.commandName == null) {
            throw new NullCommandException();
        } else if (argList.commandName.equals("add")) {
            return new AddTaskCommandParser();
        } else if (argList.commandName.equals("list")) {
            return new ListTasksCommandParser();
        } else if (argList.commandName.equals("done")) {
            return new DoneTaskCommandParser();
        } else {
            throw new UnknownCommandException(argList.commandName);
        }
    }

}

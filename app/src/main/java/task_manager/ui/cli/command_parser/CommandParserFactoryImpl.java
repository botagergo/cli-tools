package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.argument.ArgumentList;

public class CommandParserFactoryImpl implements CommandParserFactory {

    @Override
    public CommandParser getParser(ArgumentList argList) {
        if (argList.commandName == null) {
            return null;
        } else if (argList.commandName.equals("add")) {
            return new AddTaskCommandParser();
        } else if (argList.commandName.equals("list")) {
            return new ListTasksCommandParser();
        } else if (argList.commandName.equals("done")) {
            return new DoneTaskCommandParser();
        } else {
            return null;
        }
    }

}

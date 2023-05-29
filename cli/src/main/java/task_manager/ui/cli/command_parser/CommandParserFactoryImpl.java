package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.argument.ArgumentList;

public class CommandParserFactoryImpl implements CommandParserFactory {

    @Override
    public CommandParser getParser(ArgumentList argList) {
        if (argList.getCommandName() == null) {
            return null;
        } else if (argList.getCommandName().equals("add")) {
            return new AddTaskCommandParser();
        } else if (argList.getCommandName().equals("list")) {
            return new ListTasksCommandParser();
        } else if (argList.getCommandName().equals("done")) {
            return new DoneTaskCommandParser();
        } else if (argList.getCommandName().equals("clear")) {
            return new ClearCommandParser();
        } else if (argList.getCommandName().equals("delete")) {
            return new DeleteTaskCommandParser();
        } else if (argList.getCommandName().equals("modify")) {
            return new ModifyTaskCommandParser();
        }else {
            return null;
        }
    }

}

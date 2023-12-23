package task_manager.task_manager_cli.command_parser;

import task_manager.cli_lib.argument.ArgumentList;

public class CommandParserFactoryImpl implements CommandParserFactory {

    @Override
    public CommandParser getParser(ArgumentList argList) {
        return switch (argList.getCommandName()) {
            case "add" -> new AddTaskCommandParser();
            case "list" -> new ListTasksCommandParser();
            case "done" -> new DoneTaskCommandParser();
            case "clear" -> new ClearCommandParser();
            case "delete" -> new DeleteTaskCommandParser();
            case "modify" -> new ModifyTaskCommandParser();
            case "ai" -> new AICommandParser();
            default -> null;
        };
    }

}

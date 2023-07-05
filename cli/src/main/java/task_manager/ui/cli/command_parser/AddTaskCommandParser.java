package task_manager.ui.cli.command_parser;

import lombok.NonNull;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.AddTaskCommand;
import task_manager.ui.cli.command.Command;

public class AddTaskCommandParser implements CommandParser {

    @Override
    public Command parse(@NonNull Context context, ArgumentList argList) {
        return new AddTaskCommand(String.join(" ", argList.getTrailingNormalArguments()), argList.getModifyPropertyArguments());
    }

}

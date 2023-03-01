package task_manager.ui.cli.command_parser;

import java.util.HashMap;
import java.util.Map;

import task_manager.api.command.AddTaskCommand;
import task_manager.api.command.Command;

public class AddTaskCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        String taskName = String.join(" ", argList.normalArguments);
        Map<String, Object> task = new HashMap<>();
        task.put("name", taskName);
        return new AddTaskCommand(task);
    }

}

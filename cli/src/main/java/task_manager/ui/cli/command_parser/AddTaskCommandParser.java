package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.SpecialArgument;
import task_manager.ui.cli.command.AddTaskCommand;
import task_manager.ui.cli.command.Command;

import java.util.List;
import java.util.stream.Collectors;

public class AddTaskCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        List<String> tags = null;
        String status = null;
        
        List<SpecialArgument> tagArgs = argList.specialArguments.get('/');
        if (tagArgs != null) {
            tags = tagArgs.stream().map(tag -> tag.value).collect(Collectors.toList());
        }
        
        List<SpecialArgument> statusArgs = argList.specialArguments.get('%');
        if (statusArgs != null && statusArgs.size() >= 1) {
            status = statusArgs.get(statusArgs.size() - 1).value;
        }
        
        return new AddTaskCommand(String.join(" ", argList.normalArguments), tags, status);
    }

}

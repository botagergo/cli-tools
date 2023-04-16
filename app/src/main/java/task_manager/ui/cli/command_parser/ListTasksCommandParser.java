package task_manager.ui.cli.command_parser;

import java.util.List;
import java.util.stream.Collectors;
import task_manager.api.command.Command;
import task_manager.api.command.ListTasksCommand;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.SpecialArgument;

public class ListTasksCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        List<String> queries = null;
        String nameQuery = null;
        
        if (argList.specialArguments.containsKey('?')) {
            queries = argList.specialArguments.get('?').stream().map(SpecialArgument -> SpecialArgument.value).collect(Collectors.toList());
        }

        if (argList.normalArguments.size() >= 1) {
            nameQuery = String.join(" ", argList.normalArguments);
        }

        return new ListTasksCommand(queries, nameQuery);
    }

}

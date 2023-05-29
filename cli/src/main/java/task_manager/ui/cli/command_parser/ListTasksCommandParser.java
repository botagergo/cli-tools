package task_manager.ui.cli.command_parser;

import java.util.List;
import java.util.stream.Collectors;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command.ListTasksCommand;

public class ListTasksCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        List<String> queries = null;
        String nameQuery = null;
        
        if (argList.getSpecialArguments().containsKey('?')) {
            queries = argList.getSpecialArguments().get('?').stream().map(SpecialArgument -> SpecialArgument.value).collect(Collectors.toList());
        }

        if (argList.getNormalArguments().size() >= 1) {
            nameQuery = String.join(" ", argList.getNormalArguments());
        }

        return new ListTasksCommand(queries, nameQuery);
    }

}

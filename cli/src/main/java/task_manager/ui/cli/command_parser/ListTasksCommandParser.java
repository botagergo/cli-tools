package task_manager.ui.cli.command_parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import task_manager.sorter.PropertySorter;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command.ListTasksCommand;

public class ListTasksCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) throws CommandParserException {
        List<String> queries = null;
        String nameQuery = null;
        List<PropertySorter.SortingCriterion> sortingCriteria = null;
        
        if (argList.getSpecialArguments().containsKey('?')) {
            queries = argList.getSpecialArguments().get('?').stream().map(SpecialArgument -> SpecialArgument.value).collect(Collectors.toList());
        }

        if (argList.getNormalArguments().size() >= 1) {
            nameQuery = String.join(" ", argList.getNormalArguments());
        }

        for (Pair<String, List<String>> option : argList.getOptionArguments()) {
            if (option.getLeft().equals("sort")) {
                if (option.getRight().isEmpty()) {
                    throw new CommandParserException("Sorting criterion list is empty");
                }

                sortingCriteria = new ArrayList<>();
                for (String criterion : option.getRight()) {
                    if (criterion.isEmpty()) {
                        throw new CommandParserException("Empty sorting criterion");
                    }

                    boolean ascending = true;
                    if (criterion.charAt(0) == '-') {
                        ascending = false;
                        criterion = criterion.substring(1);
                    } else if (criterion.charAt(0) == '+') {
                        criterion = criterion.substring(1);
                    }

                    if (criterion.isEmpty()) {
                        throw new CommandParserException("Invalid sorting criterion");
                    }

                    sortingCriteria.add(new PropertySorter.SortingCriterion(criterion, ascending));
                }

            }
        }

        return new ListTasksCommand(queries, nameQuery, sortingCriteria);
    }

}

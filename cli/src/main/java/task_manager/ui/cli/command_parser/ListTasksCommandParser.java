package task_manager.ui.cli.command_parser;

import org.apache.commons.lang3.tuple.Pair;
import task_manager.core.data.SortingCriterion;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command.ListTasksCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListTasksCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) throws CommandParserException {
        List<String> queries = null;
        String nameQuery = null;
        List<SortingCriterion> sortingCriteria = null;
        String viewName = null;
        
        if (argList.getSpecialArguments().containsKey('?')) {
            queries = argList.getSpecialArguments().get('?').stream().map(SpecialArgument -> SpecialArgument.value).collect(Collectors.toList());
        }

        if (argList.getNormalArguments().size() >= 1) {
            nameQuery = String.join(" ", argList.getNormalArguments());
        }

        for (Pair<String, List<String>> option : argList.getOptionArguments()) {
            if (option.getLeft().equals("sort")) {
                sortingCriteria = parseSortingCriteria(option.getRight());
            } else if (option.getLeft().equals("view")) {
                viewName = parseViewName(option.getRight());
            } else {
                throw new CommandParserException("Invalid option: " + option.getLeft());
            }
        }

        return new ListTasksCommand(queries, nameQuery, sortingCriteria, argList.getPropertyArguments(), viewName);
    }

    private List<SortingCriterion> parseSortingCriteria(List<String> values) throws CommandParserException {
        if (values.isEmpty()) {
            throw new CommandParserException("Sorting criterion list is empty");
        }

        List<SortingCriterion> sortingCriteria = new ArrayList<>();
        for (String criterion : values) {
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

            sortingCriteria.add(new SortingCriterion(criterion, ascending));
        }

        return sortingCriteria;
    }

    private String parseViewName(List<String> viewNameList) throws CommandParserException {
        if (viewNameList.isEmpty()) {
            throw new CommandParserException("No view was specified");
        } else if (viewNameList.size() != 1) {
            throw new CommandParserException("Only one view can be specified");
        }

        return viewNameList.get(0);
    }

}

package task_manager.ui.cli.command_parser;

import task_manager.core.data.SortingCriterion;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.OptionArgument;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command.ListTasksCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListTasksCommandParser implements CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        List<String> queries = null;
        List<SortingCriterion> sortingCriteria = null;
        String viewName = null;
        
        if (argList.getSpecialArguments().containsKey('?')) {
            queries = argList.getSpecialArguments().get('?').stream().map(SpecialArgument -> SpecialArgument.value).collect(Collectors.toList());
        }

        if (argList.getTrailingNormalArguments().size() == 1) {
            viewName = String.join(" ", argList.getTrailingNormalArguments());
        } else if (argList.getTrailingNormalArguments().size() > 1) {
            throw new CommandParserException("One normal argument expected: view name");
        }

        for (OptionArgument optionArg : argList.getOptionArguments()) {
            if (optionArg.optionName().equals("sort")) {
                sortingCriteria = parseSortingCriteria(optionArg.values());
            } else if (optionArg.optionName().equals("view")) {
                viewName = parseViewName(optionArg.values());
            } else {
                throw new CommandParserException("Invalid option: " + optionArg.optionName());
            }
        }

        List<Integer> taskIDs = ParseUtil.getTaskIDs(context, argList.getLeadingNormalArguments());

        return new ListTasksCommand(
                queries,
                sortingCriteria,
                argList.getFilterPropertyArguments(),
                taskIDs,
                viewName);
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

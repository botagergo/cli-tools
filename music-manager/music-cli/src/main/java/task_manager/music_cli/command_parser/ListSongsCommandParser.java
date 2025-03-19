package common.music_cli.command_parser;

import common.cli.argument.ArgumentList;
import common.core.data.SortingCriterion;
import common.music_cli.Context;
import common.music_cli.command.Command;
import common.music_cli.command.ListSongsCommand;
import common.cli.argument.OptionArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListSongsCommandParser implements CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("Unexpected property arguments");
        }

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

        List<Integer> ids = ParseUtil.getIDs(context, argList.getLeadingNormalArguments());

        return new ListSongsCommand(
                queries,
                sortingCriteria,
                argList.getFilterPropertyArguments(),
                ids,
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

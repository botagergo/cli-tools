package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.OptionArgument;
import cli_tools.common.cli.argument.SpecialArgument;
import cli_tools.common.core.data.OutputFormat;
import cli_tools.common.core.data.SortingCriterion;
import cli_tools.task_manager.cli.Context;
import cli_tools.task_manager.cli.command.Command;
import cli_tools.task_manager.cli.command.ListTasksCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListTasksCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        ListTasksCommand command = new ListTasksCommand();

        if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("Unexpected property arguments");
        }

        if (argList.getSpecialArguments().containsKey('?')) {
            command.setQueries(argList.getSpecialArguments().get('?').stream().map(SpecialArgument::value).collect(Collectors.toList()));
        }

        if (argList.getTrailingNormalArguments().size() == 1) {
            command.setViewName(String.join(" ", argList.getTrailingNormalArguments()));
        } else if (argList.getTrailingNormalArguments().size() > 1) {
            throw new CommandParserException("One normal argument expected: view name");
        }

        command.setFilterPropertyArgs(argList.getFilterPropertyArguments());

        for (OptionArgument optionArg : argList.getOptionArguments()) {
            switch (optionArg.optionName()) {
                case "sort" -> command.setSortingCriteria(parseSortingCriteria(optionArg.values()));
                case "view" -> command.setViewName(parseSingleOptionValue("view", optionArg.values()));
                case "outputFormat" -> command.setOutputFormat(parseOutputFormat(optionArg.values()));
                case "hierarchical" -> command.setHierarchical(parseHierarchical(optionArg.values()));
                default -> throw new InvalidOptionException(optionArg.optionName());
            }
        }

        command.setTempIDs(ParseUtil.getTaskIDs(context, argList.getLeadingNormalArguments()));

        return command;
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

    private OutputFormat parseOutputFormat(List<String> values) throws CommandParserException {
        String outputFormat = parseSingleOptionValue("output format", values);
        switch (outputFormat) {
            case "text" -> { return OutputFormat.TEXT; }
            case "json" -> { return OutputFormat.JSON; }
            case "prettyJson" -> { return OutputFormat.PRETTY_JSON; }
            default ->
                    throw new CommandParserException("Invalid output format: " + outputFormat + "\nValid formats: text, json, prettyJson");
        }
    }

    private boolean parseHierarchical(List<String> values) throws CommandParserException {
        if (values.size() != 1) {
            throw new CommandParserException("Value of 'hierarchical must be true or false");
        }

        switch (values.get(0)) {
            case "true" -> { return true; }
            case "false" -> { return false; }
            default ->
                    throw new CommandParserException("Value of 'hierarchical must be true or false");
        }
    }

    private String parseSingleOptionValue(String optionArgumentName, List<String> valueList) throws CommandParserException {
        if (valueList.isEmpty()) {
            throw new CommandParserException("No " + optionArgumentName + " was specified");
        } else if (valueList.size() != 1) {
            throw new CommandParserException("Only one " + optionArgumentName + " can be specified");
        }

        return valueList.get(0);
    }

}

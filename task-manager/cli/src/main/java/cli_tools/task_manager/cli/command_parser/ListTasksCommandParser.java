package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.OptionArgument;
import cli_tools.common.cli.command_parser.CommandParser;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.cli.command_parser.InvalidOptionException;
import cli_tools.common.cli.command_parser.ParseUtil;
import cli_tools.common.core.data.OutputFormat;
import cli_tools.common.core.data.SortingCriterion;
import cli_tools.common.cli.command.Command;
import cli_tools.task_manager.cli.command.ListTasksCommand;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ListTasksCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
            ListTasksCommand command = new ListTasksCommand();

        if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("unexpected property arguments");
        }

        if (argList.getTrailingNormalArguments().size() == 1) {
            command.setViewName(String.join(" ", argList.getTrailingNormalArguments()));
        } else if (argList.getTrailingNormalArguments().size() > 1) {
            throw new CommandParserException("one normal argument expected: view name");
        }

        if (!argList.getFilterPropertyArguments().isEmpty()) {
            command.setFilterPropertyArgs(argList.getFilterPropertyArguments());
        }

        for (OptionArgument optionArg : argList.getOptionArguments()) {
            switch (optionArg.optionName()) {
                case "sort" -> command.setSortingCriteria(parseSortingCriteria(optionArg.values()));
                case "view" -> command.setViewName(parseSingleOptionValue("view", optionArg.values()));
                case "properties" -> command.setProperties(parseProperties(optionArg.values()));
                case "outputFormat" -> command.setOutputFormat(parseOutputFormat(optionArg.values()));
                case "hierarchical" -> command.setHierarchical(parseHierarchical(optionArg.values()));
                default -> throw new InvalidOptionException(optionArg.optionName());
            }
        }

        command.setTempIDs(ParseUtil.getTempIds(context, argList.getLeadingNormalArguments()));

        return command;
    }


    private List<SortingCriterion> parseSortingCriteria(List<String> values) throws CommandParserException {
        if (values.isEmpty()) {
            throw new CommandParserException("sorting criterion list is empty");
        }

        List<SortingCriterion> sortingCriteria = new ArrayList<>();
        for (String criterion : values) {
            if (criterion.isEmpty()) {
                throw new CommandParserException("empty sorting criterion");
            }

            boolean ascending = true;
            if (criterion.charAt(0) == '-') {
                ascending = false;
                criterion = criterion.substring(1);
            } else if (criterion.charAt(0) == '+') {
                criterion = criterion.substring(1);
            }

            if (criterion.isEmpty()) {
                throw new CommandParserException("invalid sorting criterion");
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
                    throw new CommandParserException("invalid output format: " + outputFormat + "\nvalid formats: text, json, prettyJson");
        }
    }

    private boolean parseHierarchical(List<String> values) throws CommandParserException {
        if (values.size() != 1) {
            throw new CommandParserException("value of 'hierarchical must be true or false");
        }

        switch (values.get(0)) {
            case "true" -> { return true; }
            case "false" -> { return false; }
            default ->
                    throw new CommandParserException("value of .hierarchical must be true or false");
        }
    }

    private List<String> parseProperties(List<String> values) {
        if (values.isEmpty()) {
            log.warn("empty list received in .properties option, ignoring");
            return null;
        }
        return values;
    }

    private String parseSingleOptionValue(String optionArgumentName, List<String> valueList) throws CommandParserException {
        if (valueList.isEmpty()) {
            throw new CommandParserException("no " + optionArgumentName + " was specified");
        } else if (valueList.size() != 1) {
            throw new CommandParserException("only one " + optionArgumentName + " can be specified");
        }

        return valueList.get(0);
    }

}

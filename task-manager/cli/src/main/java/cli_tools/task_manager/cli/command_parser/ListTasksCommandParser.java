package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.OptionArgument;
import cli_tools.common.cli.argument.SpecialArgument;
import cli_tools.common.cli.command.Command;
import cli_tools.common.cli.command_parser.CommandParser;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.cli.command_parser.InvalidOptionException;
import cli_tools.common.cli.command_parser.ParseUtil;
import cli_tools.common.core.data.OutputFormat;
import cli_tools.common.core.data.SortingCriterion;
import cli_tools.task_manager.cli.command.ListTasksCommand;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class ListTasksCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        ListTasksCommand command = new ListTasksCommand();

        if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("Command 'list' does not accept modify property arguments");
        }

        if (argList.getTrailingPositionalArguments().size() == 1) {
            command.setViewName(String.join(" ", argList.getTrailingPositionalArguments()));
        } else if (argList.getTrailingPositionalArguments().size() > 1) {
            throw new CommandParserException("One positional argument expected: view name");
        }

        if (!argList.getFilterPropertyArguments().isEmpty()) {
            command.setFilterPropertyArgs(argList.getFilterPropertyArguments());
        }

        for (OptionArgument optionArg : argList.getOptionArguments()) {
            switch (optionArg.optionName()) {
                case "sort" -> command.setSortingCriteria(parseSortingCriteria(optionArg.values()));
                case "view" -> command.setViewName(parseSingleOptionValue("view", optionArg.values()));
                case "properties" -> {
                    command.setProperties(parseProperties(optionArg.values()));
                    command.setOverwriteProperties(true);
                }
                case "outputFormat" -> command.setOutputFormat(parseOutputFormat(optionArg.values()));
                case "hierarchical" -> command.setHierarchical(parseBoolean("hierarchical", optionArg.values()));
                case "listDone" -> command.setListDone(parseBoolean("listDone", optionArg.values()));
                default -> throw new InvalidOptionException(optionArg.optionName());
            }
        }

        for (SpecialArgument specialArgument : argList.getSpecialArguments()) {
            switch (specialArgument.type()) {
                case '$' -> parsePropertiesSpecialArg(specialArgument.value(), command);
                default -> throw new CommandParserException("Invalid special argument type for command 'list': " + specialArgument.type());
            }
        }

        command.setTempIDs(ParseUtil.getTempIds(context, argList.getLeadingPositionalArguments()));

        return command;
    }

    private void parsePropertiesSpecialArg(String propertiesStr, ListTasksCommand listTasksCommand) {
        boolean overwriteProperties = true;
        if (propertiesStr.startsWith("+")) {
            propertiesStr = propertiesStr.substring(1);
            overwriteProperties = false;
        }

        String[] properties = propertiesStr.isEmpty() ? new String[0] : propertiesStr.split(",");
        if (overwriteProperties) {
            listTasksCommand.setProperties(new ArrayList<>(Arrays.asList(properties)));
            listTasksCommand.setOverwriteProperties(true);
        } else {
            if (listTasksCommand.getProperties() == null) {
                listTasksCommand.setProperties(new ArrayList<>(Arrays.asList(properties)));
            } else {
                listTasksCommand.getProperties().addAll(List.of(properties));
            }
            if (listTasksCommand.getOverwriteProperties() != null) {
                listTasksCommand.setOverwriteProperties(true);
            }
        }
    }


    private List<SortingCriterion> parseSortingCriteria(List<String> values) throws CommandParserException {
        values = parseListOptionValue(values);

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
                throw new CommandParserException("empty sorting criterion");
            }

            sortingCriteria.add(new SortingCriterion(criterion, ascending));
        }

        return sortingCriteria;
    }

    private OutputFormat parseOutputFormat(List<String> values) throws CommandParserException {
        String outputFormat = parseSingleOptionValue("outputFormat", values);
        switch (outputFormat) {
            case "text" -> {
                return OutputFormat.TEXT;
            }
            case "json" -> {
                return OutputFormat.JSON;
            }
            case "prettyJson" -> {
                return OutputFormat.PRETTY_JSON;
            }
            default ->
                    throw new CommandParserException("invalid output format: " + outputFormat + "\nvalid values: text, json, prettyJson");
        }
    }

    private List<String> parseProperties(List<String> values) {
        values = parseListOptionValue(values);
        if (values.isEmpty()) {
            log.warn("empty list received in option 'properties', ignoring");
            return null;
        }
        return values;
    }

}

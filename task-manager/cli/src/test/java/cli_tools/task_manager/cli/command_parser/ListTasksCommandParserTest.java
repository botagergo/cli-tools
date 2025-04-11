package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.OptionArgument;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.argument.SpecialArgument;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.core.data.OutputFormat;
import cli_tools.common.core.data.SortingCriterion;
import cli_tools.common.core.data.property.Affinity;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.ListTasksCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.testng.Assert.*;

public class ListTasksCommandParserTest {

    private final ListTasksCommandParser parser = new ListTasksCommandParser();
    private final TaskManagerContext context = new TaskManagerContext();

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(), List.of(), List.of()));
        assertNull(command.getViewName());
        assertNull(command.getHierarchical());
        assertNull(command.getProperties());
        assertNull(command.getSortingCriteria());
        assertEquals(command.getTempIDs().size(), 0);
        assertNull(command.getFilterPropertyArgs());
        assertNull(command.getOutputFormat());

    }

    @Test
    public void test_parse_onePositionalArg() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of(), List.of("viewName"), new LinkedHashMap<>(), List.of(), List.of()));
        assertEquals(command.getViewName(), "viewName");
    }

    @Test
    public void test_parse_twoPositionalArgs_throws() {
        assertThrows(CommandParserException.class, () ->
                parse(getArgList(List.of(), List.of("view1", "view2"), new LinkedHashMap<>(), List.of(), List.of())));
    }

    @Test
    public void test_parse_oneTempId() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of("1"), List.of(), new LinkedHashMap<>(), List.of(), List.of()));
        assertEquals(command.getTempIDs(), List.of(1));
    }

    @Test
    public void test_parse_multipleTempIds() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of("1", "3", "2"), List.of(), new LinkedHashMap<>(), List.of(), List.of()));
        assertEquals(command.getTempIDs(), List.of(1, 3, 2));
    }

    @Test
    public void test_parse_hierarchical() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("hierarchical", List.of("true"))),
                List.of()));
        assertEquals(command.getHierarchical(), true);
        command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("hierarchical", List.of("false"))),
                List.of()));
        assertEquals(command.getHierarchical(), false);
    }

    @Test
    public void test_parse_properties() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("properties", List.of())),
                List.of()));
        assertNull(command.getProperties());
        command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("properties", List.of("id"))),
                List.of()));
        assertEquals(command.getProperties(), List.of("id"));
        command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("properties", List.of("id", "name", "status"))),
                List.of()));
        assertEquals(command.getProperties(), List.of("id", "name", "status"));
    }

    @Test
    public void test_parse_sort() throws CommandParserException {
        assertThrows(CommandParserException.class, () -> parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("sort", List.of())),
                List.of())));
        ListTasksCommand command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("sort", List.of("-name"))),
                List.of()));
        assertEquals(command.getSortingCriteria(), List.of(new SortingCriterion("name", false)));
        command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("sort", List.of("-name", "id", "+priority"))),
                List.of()));
        assertEquals(command.getSortingCriteria(), List.of(
                new SortingCriterion("name", false),
                new SortingCriterion("id", true),
                new SortingCriterion("priority", true)
        ));
    }

    @Test
    public void test_parse_filterPropertyArgs() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(), List.of(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "status", null, List.of("backlog")),
                new PropertyArgument(Affinity.NEGATIVE, "p", "in", List.of()),
                new PropertyArgument(Affinity.NEUTRAL, "property", "in", List.of("val1", "val2"))
        )));
        assertEquals(command.getFilterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "status", null, List.of("backlog")),
                new PropertyArgument(Affinity.NEGATIVE, "p", "in", List.of()),
                new PropertyArgument(Affinity.NEUTRAL, "property", "in", List.of("val1", "val2"))
        ));
    }

    @Test
    public void test_parse_outputFormat() throws CommandParserException {
        assertThrows(CommandParserException.class, () -> parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("outputFormat", List.of("jpg"))),
                List.of())));
        assertThrows(CommandParserException.class, () -> parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("outputFormat", List.of())),
                List.of())));
        assertThrows(CommandParserException.class, () -> parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("outputFormat", List.of("text", "json"))),
                List.of())));
        ListTasksCommand command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("outputFormat", List.of("text"))),
                List.of()));
        assertEquals(command.getOutputFormat(), OutputFormat.TEXT);
        command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("outputFormat", List.of("json"))),
                List.of()));
        assertEquals(command.getOutputFormat(), OutputFormat.JSON);
        command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(),
                List.of(new OptionArgument("outputFormat", List.of("prettyJson"))),
                List.of()));
        assertEquals(command.getOutputFormat(), OutputFormat.PRETTY_JSON);
    }

    @Test
    public void test_parse_invalidOption_throws() {
        assertThrows(CommandParserException.class, () -> parse(getArgList(
                List.of(),
                List.of("my", "task"),
                new LinkedHashMap<>(),
                List.of(new OptionArgument("invalid-option", List.of("some-value"))),
                List.of()
        )));
    }

    @Test
    public void test_parse_complex() throws CommandParserException {
        ListTasksCommand command = parse(
                getArgList(
                        List.of("1", "3"),
                        List.of("view1"),
                        new LinkedHashMap<>(),
                        List.of(
                                new OptionArgument("sort", List.of("prop1"))),
                        List.of(
                                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
                        )
                ));
        assertEquals(command.getViewName(), "view1");
        assertEquals(command.getTempIDs(), List.of(1, 3));
        assertEquals(command.getSortingCriteria(), List.of(
                new SortingCriterion("prop1", true)
        ));
        assertEquals(command.getFilterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
        ));
    }

    private ListTasksCommand parse(ArgumentList argList) throws CommandParserException {
        return (ListTasksCommand) parser.parse(context, argList);
    }

    private ArgumentList getArgList(
            @NonNull List<String> leadingPositionalArgs,
            @NonNull List<String> trailingPositionalArgs,
            @NonNull LinkedHashMap<Character, List<SpecialArgument>> specialArgs,
            @NonNull List<OptionArgument> optionArgs,
            @NonNull List<PropertyArgument> filterPropertyArgs
    ) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("list");
        argList.setLeadingPositionalArguments(leadingPositionalArgs);
        argList.setTrailingPositionalArguments(trailingPositionalArgs);
        argList.setSpecialArguments(specialArgs);
        argList.setOptionArguments(optionArgs);
        argList.setFilterPropertyArguments(filterPropertyArgs);
        return argList;
    }
}

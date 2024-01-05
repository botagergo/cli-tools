package task_manager.task_manager_cli.command_parser;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import task_manager.core.data.SortingCriterion;
import task_manager.core.property.Affinity;
import task_manager.cli_lib.argument.ArgumentList;
import task_manager.cli_lib.argument.OptionArgument;
import task_manager.cli_lib.argument.PropertyArgument;
import task_manager.cli_lib.argument.SpecialArgument;
import task_manager.task_manager_cli.Context;
import task_manager.task_manager_cli.command.ListTasksCommand;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class ListTasksCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(), List.of(), List.of()));
        assertNull(command.getViewName());
        assertNull(command.getQueries());
    }

    @Test
    public void test_parse_oneNormalArg() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of(), List.of("viewName"), new LinkedHashMap<>(), List.of(), List.of()));
        assertEquals(command.getViewName(), "viewName");
        assertNull(command.getQueries());
    }

    @Test
    public void test_parse_twoNormalArgs_throws() {
        assertThrows(CommandParserException.class, () ->
                parse(getArgList(List.of(), List.of("view1", "view2"), new LinkedHashMap<>(), List.of(), List.of())));
    }

    @Test
    public void test_parse_oneQueryArg() throws CommandParserException {
        ListTasksCommand command = parse(
                getArgList(
                        List.of(),
                        List.of(),
                        new LinkedHashMap<>(Map.of('?', List.of(
                                new SpecialArgument('?', "text='my task'")))),
                        List.of(),
                        List.of()));
        assertNull(command.getViewName());
        assertEquals(command.getQueries(), List.of("text='my task'"));
    }

    @Test
    public void test_parse_twoQueryArgs() throws CommandParserException {
        ListTasksCommand command = parse(
                getArgList(
                        List.of(),
                        List.of(),
                        new LinkedHashMap<>(Map.of('?', List.of(
                                new SpecialArgument('?', "text='my task'"),
                                new SpecialArgument('?', "text='other task'")))),
                        List.of(),
                        List.of()
                ));
        assertNull(command.getViewName());
        assertEquals(command.getQueries(), List.of("text='my task'", "text='other task'"));
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
            @NonNull List<String> leadingNormalArgs,
            @NonNull List<String> trailingNormalArgs,
            @NonNull LinkedHashMap<Character, List<SpecialArgument>> specialArgs,
            @NonNull List<OptionArgument> optionArgs,
            @NonNull List<PropertyArgument> filterPropertyArgs
    ) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("list");
        argList.setLeadingNormalArguments(leadingNormalArgs);
        argList.setTrailingNormalArguments(trailingNormalArgs);
        argList.setSpecialArguments(specialArgs);
        argList.setOptionArguments(optionArgs);
        argList.setFilterPropertyArguments(filterPropertyArgs);
        return argList;
    }

    private final ListTasksCommandParser parser = new ListTasksCommandParser();
    private final Context context = new Context();
}

package common.music_cli.command_parser;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import common.cli.argument.ArgumentList;
import common.core.data.SortingCriterion;
import common.core.property.Affinity;
import common.music_cli.Context;
import common.music_cli.command.ListSongsCommand;
import common.cli.argument.OptionArgument;
import common.cli.argument.PropertyArgument;
import common.cli.argument.SpecialArgument;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class ListSongsCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        ListSongsCommand command = parse(getArgList(List.of(), List.of(), new LinkedHashMap<>(), List.of(), List.of()));
        assertNull(command.viewName());
        assertNull(command.queries());
    }

    @Test
    public void test_parse_oneNormalArg() throws CommandParserException {
        ListSongsCommand command = parse(getArgList(List.of(), List.of("viewName"), new LinkedHashMap<>(), List.of(), List.of()));
        assertEquals(command.viewName(), "viewName");
        assertNull(command.queries());
    }

    @Test
    public void test_parse_twoNormalArgs_throws() {
        assertThrows(CommandParserException.class, () ->
                parse(getArgList(List.of(), List.of("view1", "view2"), new LinkedHashMap<>(), List.of(), List.of())));
    }

    @Test
    public void test_parse_oneQueryArg() throws CommandParserException {
        ListSongsCommand command = parse(
                getArgList(
                        List.of(),
                        List.of(),
                        new LinkedHashMap<>(Map.of('?', List.of(
                                new SpecialArgument('?', "text='my task'")))),
                        List.of(),
                        List.of()));
        assertNull(command.viewName());
        assertEquals(command.queries(), List.of("text='my task'"));
    }

    @Test
    public void test_parse_twoQueryArgs() throws CommandParserException {
        ListSongsCommand command = parse(
                getArgList(
                        List.of(),
                        List.of(),
                        new LinkedHashMap<>(Map.of('?', List.of(
                                new SpecialArgument('?', "text='my task'"),
                                new SpecialArgument('?', "text='other task'")))),
                        List.of(),
                        List.of()
                ));
        assertNull(command.viewName());
        assertEquals(command.queries(), List.of("text='my task'", "text='other task'"));
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
        ListSongsCommand command = parse(
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
        assertEquals(command.viewName(), "view1");
        assertEquals(command.tempIDs(), List.of(1, 3));
        assertEquals(command.sortingCriteria(), List.of(
                new SortingCriterion("prop1", true)
        ));
        assertEquals(command.filterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
        ));
    }

    private ListSongsCommand parse(ArgumentList argList) throws CommandParserException {
        return (ListSongsCommand) parser.parse(context, argList);
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

    private final ListSongsCommandParser parser = new ListSongsCommandParser();
    private final Context context = new Context();
}

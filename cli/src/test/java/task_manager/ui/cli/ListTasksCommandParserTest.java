package task_manager.ui.cli;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.OptionArgument;
import task_manager.ui.cli.argument.SpecialArgument;
import task_manager.ui.cli.command.ListTasksCommand;
import task_manager.ui.cli.command_parser.CommandParserException;
import task_manager.ui.cli.command_parser.ListTasksCommandParser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class ListTasksCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of(), new LinkedHashMap<>(), List.of()));
        assertNull(command.nameQuery());
        assertNull(command.queries());
    }

    @Test
    public void test_parse_twoNormalArgs() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of("my", "task"), new LinkedHashMap<>(), List.of()));
        assertEquals(command.nameQuery(), "my task");
        assertNull(command.queries());
    }

    @Test
    public void test_parse_oneQueryArg() throws CommandParserException {
        ListTasksCommand command = parse(
                getArgList(
                        List.of(),
                        new LinkedHashMap<>(Map.of('?', List.of(
                                new SpecialArgument('?', "text='my task'")))),
                        List.of()));
        assertNull(command.nameQuery());
        assertEquals(command.queries(), List.of("text='my task'"));
    }

    @Test
    public void test_parse_twoQueryArgs() throws CommandParserException {
        ListTasksCommand command = parse(
                getArgList(
                        List.of(),
                        new LinkedHashMap<>(Map.of('?', List.of(
                                new SpecialArgument('?', "text='my task'"),
                                new SpecialArgument('?', "text='other task'")))),
                        List.of()));
        assertNull(command.nameQuery());
        assertEquals(command.queries(), List.of("text='my task'", "text='other task'"));
    }

    @Test
    public void test_parse_invalidOption_throws() {
        assertThrows(CommandParserException.class, () ->
                parse(getArgList(
                    List.of("my", "task"),
                    new LinkedHashMap<>(),
                    List.of(new OptionArgument("invalid-option", List.of("some-value"))))));
    }

    @Test
    public void test_parse_view() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(
                List.of("my", "task"),
                new LinkedHashMap<>(),
                List.of(new OptionArgument("view", List.of("test-view")))));
        assertEquals(command.nameQuery(), "my task");
        assertEquals(command.viewName(), "test-view");
        assertNull(command.queries());
    }

    private ListTasksCommand parse(ArgumentList argList) throws CommandParserException {
        return (ListTasksCommand) parser.parse(argList);
    }

    private ArgumentList getArgList(
            @NonNull List<String> normalArgs,
            @NonNull LinkedHashMap<Character, List<SpecialArgument>> specialArgs,
            @NonNull List<OptionArgument> optionArgs
    ) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("list");
        argList.setNormalArguments(normalArgs);
        argList.setSpecialArguments(specialArgs);
        argList.setOptionArguments(optionArgs);
        return argList;
    }

    final ListTasksCommandParser parser = new ListTasksCommandParser();
}

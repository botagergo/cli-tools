package task_manager.ui.cli;

import org.testng.annotations.*;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.SpecialArgument;
import task_manager.ui.cli.command.ListTasksCommand;
import task_manager.ui.cli.command_parser.CommandParserException;
import task_manager.ui.cli.command_parser.ListTasksCommandParser;

import static org.testng.Assert.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListTasksCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of(), new LinkedHashMap<>()));
        assertNull(command.nameQuery());
        assertNull(command.queries());
    }

    @Test
    public void test_parse_twoNormalArgs() throws CommandParserException {
        ListTasksCommand command = parse(getArgList(List.of("my", "task"), new LinkedHashMap<>()));
        assertEquals(command.nameQuery(), "my task");
        assertNull(command.queries());
    }

    @Test
    public void test_parse_oneQueryArg() throws CommandParserException {
        ListTasksCommand command = parse(
                getArgList(List.of(),
                        new LinkedHashMap<>(Map.of('?', List.of(
                                new SpecialArgument('?', "name='my task'"))))));
        assertNull(command.nameQuery());
        assertEquals(command.queries(), List.of("name='my task'"));
    }

    @Test
    public void test_parse_twoQueryArgs() throws CommandParserException {
        ListTasksCommand command = parse(
                getArgList(List.of(),
                        new LinkedHashMap<>(Map.of('?', List.of(
                                new SpecialArgument('?', "name='my task'"),
                                new SpecialArgument('?', "name='other task'"))))));
        assertNull(command.nameQuery());
        assertEquals(command.queries(), List.of("name='my task'", "name='other task'"));
    }

    private ListTasksCommand parse(ArgumentList argList) throws CommandParserException {
        return (ListTasksCommand) parser.parse(argList);
    }

    private ArgumentList getArgList(List<String> normalArgs,
            LinkedHashMap<Character, List<SpecialArgument>> specialArgs) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("list");
        argList.setNormalArguments(normalArgs);
        argList.setSpecialArguments(specialArgs);
        return argList;
    }

    final ListTasksCommandParser parser = new ListTasksCommandParser();
}

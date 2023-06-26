package task_manager.ui.cli.command_parser;

import org.testng.annotations.Test;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.DeleteTaskCommand;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class DeleteTaskCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList());
        assertEquals(command.taskIDs().size(), 0);
    }

    @Test
    public void test_parse_oneTaskID() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList("1"));
        assertEquals(command.taskIDs(), List.of(1));
    }

    @Test
    public void test_parse_multipleTaskIDs() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList("3", "111", "333"));
        assertEquals(command.taskIDs(), List.of(3, 111, 333));
    }

    @Test
    public void test_parse_invalidTaskID() {
        assertThrows(CommandParserException.class, () -> parse(getArgList("1", "asdf", "2")));
    }

    private DeleteTaskCommand parse(ArgumentList argList) throws CommandParserException {
        return (DeleteTaskCommand) parser.parse(argList);
    }

    private ArgumentList getArgList(String... param) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("delete");
        argList.setNormalArguments(Arrays.asList(param));
        return argList;
    }

    final DeleteTaskCommandParser parser = new DeleteTaskCommandParser();
}

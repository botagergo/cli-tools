package task_manager.ui.cli;

import org.testng.annotations.*;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.DoneTaskCommand;
import task_manager.ui.cli.command_parser.DoneTaskCommandParser;

import static org.testng.Assert.*;

import java.util.Arrays;

public class DoneTaskCommandParserTest {

    @Test
    public void test_parse_noArgs() {
        DoneTaskCommand command = parse(getArgList());
        assertEquals(command.query(), "");
    }

    @Test
    public void test_parse_oneNormalArg() {
        DoneTaskCommand command = parse(getArgList("task"));
        assertEquals(command.query(), "task");
    }

    @Test
    public void test_parse_oneEmptyNormalArg() {
        DoneTaskCommand command = parse(getArgList(""));
        assertEquals(command.query(), "");
    }

    @Test
    public void test_parse_multipleNormalArgs() {
        DoneTaskCommand command = parse(getArgList("my", "simple", "task"));
        assertEquals(command.query(), "my simple task");
    }

    @Test
    public void test_parse_multipleNormalArgsWithWhitespace() {
        DoneTaskCommand command = parse(getArgList("my ", "simple", "  task"));
        assertEquals(command.query(), "my  simple   task");
    }

    private DoneTaskCommand parse(ArgumentList argList) {
        return (DoneTaskCommand) parser.parse(argList);
    }

    private ArgumentList getArgList(String... param) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("done");
        argList.setNormalArguments(Arrays.asList(param));
        return argList;
    }

    final DoneTaskCommandParser parser = new DoneTaskCommandParser();
}

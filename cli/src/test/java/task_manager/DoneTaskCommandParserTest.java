package task_manager;

import org.testng.annotations.*;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.DoneTaskCommand;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;
import task_manager.ui.cli.command_parser.DoneTaskCommandParser;

import static org.testng.Assert.*;

import java.util.Arrays;

public class DoneTaskCommandParserTest {

    @Test
    public void testNoNormalArgs() {
        assertEquals(((DoneTaskCommand) parser.parse(getArgList())).query, "");
    }

    @Test
    public void testOneNormalArg() {
        assertEquals(((DoneTaskCommand) parser.parse(getArgList("task"))).query, "task");
    }

    @Test
    public void testMultipleNormalArgs() {
        assertEquals(((DoneTaskCommand) parser.parse(getArgList("my", "simple", "task"))).query,
            "my simple task");
    }

    @Test
    public void testMultipleNormalArgsWithWhitespace() {
        assertEquals(((DoneTaskCommand) parser.parse(getArgList("my ", "simple", "  task"))).query,
            "my  simple   task");
    }

    private ArgumentList getArgList(String... param) {
        ArgumentList argList = new ArgumentList();
        argList.commandName = "done";
        argList.normalArguments = Arrays.asList(param);
        return argList;
    }

    CommandParserFactoryImpl commandParserFactory = new CommandParserFactoryImpl();
    final DoneTaskCommandParser parser = new DoneTaskCommandParser();
}

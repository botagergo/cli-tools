package task_manager;

import org.testng.annotations.*;

import task_manager.api.command.AddTaskCommand;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command_parser.AddTaskCommandParser;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;

import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.Map;

public class AddTaskCommandParserTest {

    @Test
    public void testNoNormalArgs() {
        Map<String, Object> task = ((AddTaskCommand) parser.parse(getArgList())).task;
        assertEquals(task.get("name"), "");
    }

    @Test
    public void testOneNormalArg() {
        Map<String, Object> task = ((AddTaskCommand) parser.parse(getArgList("task"))).task;
        assertEquals(task.get("name"), "task");
    }

    @Test
    public void testMultipleNormalArgs() {
        Map<String, Object> task =
                ((AddTaskCommand) parser.parse(getArgList("my", "simple", "task"))).task;
        assertEquals(task.get("name"), "my simple task");
    }

    @Test
    public void testMultipleNormalArgsWithWhitespace() {
        Map<String, Object> task =
                ((AddTaskCommand) parser.parse(getArgList("my ", "simple", "  task"))).task;
        assertEquals(task.get("name"), "my  simple   task");
    }

    private ArgumentList getArgList(String... param) {
        ArgumentList argList = new ArgumentList();
        argList.commandName = "add";
        argList.normalArguments = Arrays.asList(param);
        return argList;
    }

    CommandParserFactoryImpl commandParserFactory = new CommandParserFactoryImpl();
    AddTaskCommandParser parser = new AddTaskCommandParser();
}

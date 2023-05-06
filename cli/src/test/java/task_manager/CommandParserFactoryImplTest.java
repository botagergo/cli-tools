package task_manager;

import org.testng.annotations.*;

import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command_parser.AddTaskCommandParser;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;
import task_manager.ui.cli.command_parser.DoneTaskCommandParser;
import task_manager.ui.cli.command_parser.ListTasksCommandParser;

import static org.testng.Assert.*;

public class CommandParserFactoryImplTest {

    @Test
    public void testAdd() {
        assertTrue(
                commandParserFactory.getParser(getArgList("add")) instanceof AddTaskCommandParser);
    }

    @Test
    public void testDone() {
        assertTrue(commandParserFactory
                .getParser(getArgList("done")) instanceof DoneTaskCommandParser);
    }

    @Test
    public void testList() {
        assertTrue(commandParserFactory
                .getParser(getArgList("list")) instanceof ListTasksCommandParser);
    }

    @Test
    public void testUnknown() {
        assertNull(commandParserFactory.getParser(getArgList("unknown")));
        assertNull(commandParserFactory.getParser(getArgList(null)));
    }

    private ArgumentList getArgList(String commandName) {
        ArgumentList argList = new ArgumentList();
        argList.commandName = commandName;
        return argList;
    }

    final CommandParserFactoryImpl commandParserFactory = new CommandParserFactoryImpl();
}

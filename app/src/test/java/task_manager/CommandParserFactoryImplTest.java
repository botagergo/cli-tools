package task_manager;

import org.testng.annotations.*;

import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command_parser.AddTaskCommandParser;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;
import task_manager.ui.cli.command_parser.DoneTaskCommandParser;
import task_manager.ui.cli.command_parser.ListTasksCommandParser;
import task_manager.ui.cli.command_parser.NullCommandException;
import task_manager.ui.cli.command_parser.UnknownCommandException;

import static org.testng.Assert.*;

public class CommandParserFactoryImplTest {

    @Test
    public void testAdd() throws Exception {
        assertTrue(
                commandParserFactory.getParser(getArgList("add")) instanceof AddTaskCommandParser);
    }

    @Test
    public void testDone() throws Exception {
        assertTrue(commandParserFactory
                .getParser(getArgList("done")) instanceof DoneTaskCommandParser);
    }

    @Test
    public void testList() throws Exception {
        assertTrue(commandParserFactory
                .getParser(getArgList("list")) instanceof ListTasksCommandParser);
    }

    @Test
    public void testUnknown() throws Exception {
        assertThrows(UnknownCommandException.class, () -> {
            commandParserFactory.getParser(getArgList("unknown"));
        });
        assertThrows(NullCommandException.class, () -> {
            commandParserFactory.getParser(getArgList(null));
        });
    }

    private ArgumentList getArgList(String commandName) {
        ArgumentList argList = new ArgumentList();
        argList.commandName = commandName;
        return argList;
    }

    CommandParserFactoryImpl commandParserFactory = new CommandParserFactoryImpl();
}

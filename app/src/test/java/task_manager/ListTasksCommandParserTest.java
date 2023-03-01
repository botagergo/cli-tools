package task_manager;

import org.testng.annotations.*;

import task_manager.api.command.ListTasksCommand;
import task_manager.ui.cli.command_parser.ArgumentList;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;
import task_manager.ui.cli.command_parser.ListTasksCommandParser;

import static org.testng.Assert.*;

public class ListTasksCommandParserTest {

    @Test
    public void testNoNormalArgs() {
        ListTasksCommand cmd = (ListTasksCommand) parser.parse(getArgList());
        assertNotNull(cmd);
    }

    private ArgumentList getArgList() {
        ArgumentList argList = new ArgumentList();
        argList.commandName = "list";
        return argList;
    }

    CommandParserFactoryImpl commandParserFactory = new CommandParserFactoryImpl();
    ListTasksCommandParser parser = new ListTasksCommandParser();
}

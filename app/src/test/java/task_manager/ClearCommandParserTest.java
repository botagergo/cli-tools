package task_manager;

import org.testng.annotations.*;

import task_manager.api.command.ClearCommand;
import task_manager.api.command.DoneTaskCommand;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command_parser.ClearCommandParser;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;
import task_manager.ui.cli.command_parser.DoneTaskCommandParser;

import static org.testng.Assert.*;

import java.util.Arrays;

public class ClearCommandParserTest {

    @Test
    public void testNoNormalArgs() {
        ClearCommand cmd = (ClearCommand) parser.parse(getArgList());
        assertNotNull(cmd);
    }

    private ArgumentList getArgList() {
        ArgumentList argList = new ArgumentList();
        argList.commandName = "clear";
        return argList;
    }

    ClearCommandParser parser = new ClearCommandParser();

}

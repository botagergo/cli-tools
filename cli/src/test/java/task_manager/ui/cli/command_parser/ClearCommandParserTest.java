package task_manager.ui.cli.command_parser;

import org.testng.annotations.Test;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.ClearCommand;

import static org.testng.Assert.assertNotNull;

public class ClearCommandParserTest {

    @Test
    public void test_parse_noArgs() {
        ClearCommand cmd = (ClearCommand) parser.parse(getArgList());
        assertNotNull(cmd);
    }

    private ArgumentList getArgList() {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("clear");
        return argList;
    }

    final ClearCommandParser parser = new ClearCommandParser();

}

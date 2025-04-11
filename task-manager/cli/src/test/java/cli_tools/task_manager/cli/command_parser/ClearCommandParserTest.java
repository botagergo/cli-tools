package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.ClearCommand;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class ClearCommandParserTest {

    private final ClearCommandParser parser = new ClearCommandParser();
    private final TaskManagerContext context = new TaskManagerContext();

    @Test
    void test_parse_noArgs() {
        ClearCommand cmd = (ClearCommand) parser.parse(context, getArgList());
        assertNotNull(cmd);
    }

    private ArgumentList getArgList() {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("clear");
        return argList;
    }

}

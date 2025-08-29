package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.OptionArgument;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.ListLabelCommand;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ListLabelCommandParserTest {

    private final ListLabelCommandParser parser = new ListLabelCommandParser();
    private final TaskManagerContext context = new TaskManagerContext();

    @Test
    void test_parse_noArgs() throws CommandParserException {
        ListLabelCommand cmd = (ListLabelCommand) parser.parse(context, getArgList(null));
        assertNotNull(cmd);
    }

    @Test
    void test_parse_labelTypes() throws CommandParserException {
        ListLabelCommand cmd = (ListLabelCommand) parser.parse(context, getArgList(List.of("tag", "status")));
        assertNotNull(cmd);
        assertEquals(cmd.getLabelTypes(), List.of("tag", "status"));
    }

    private ArgumentList getArgList(List<String> labelTypes) {
        ArgumentList argList = new ArgumentList();
        if (labelTypes != null) {
            argList.setOptionArguments(List.of(new OptionArgument("type", labelTypes)));
        }
        argList.setCommandName("listLabel");
        return argList;
    }

}

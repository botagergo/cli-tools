package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.OptionArgument;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.AddLabelCommand;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class AddLabelCommandParserTest {

    private final AddLabelCommandParser parser = new AddLabelCommandParser();
    private final TaskManagerContext context = new TaskManagerContext();

    @Test
    void test_parse_noArgs() {
        assertThrows(CommandParserException.class, () -> parser.parse(context, getArgList(null, null)));
    }

    @Test
    void test_parse_trailingPositionalArgs() throws CommandParserException {
        AddLabelCommand command = (AddLabelCommand) parser.parse(context, getArgList("status", List.of("label1", "label2")));
        assertEquals(command.getGetLabelTexts(), List.of("label1", "label2"));
    }

    @Test
    void test_parse_noTrailingPositionalArgs() throws CommandParserException {
        AddLabelCommand command = (AddLabelCommand) parser.parse(context, getArgList("status", null));
        assertEquals(command.getGetLabelTexts(), List.of());
    }

    @Test
    void test_parse_noLabelType() {
        assertThrows(CommandParserException.class, () -> parser.parse(context, getArgList(null, List.of("label1", "label2"))));
    }

    private ArgumentList getArgList(String type, List<String> trailingPositionalArguments) {
        ArgumentList argList = new ArgumentList();
        if (trailingPositionalArguments != null) {
            argList.setTrailingPositionalArguments(trailingPositionalArguments);
        }
        argList.setCommandName("listLabel");
        if (type != null) {
            argList.setOptionArguments(List.of(new OptionArgument("type", List.of(type))));
        }
        return argList;
    }

}

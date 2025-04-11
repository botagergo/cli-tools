package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.core.data.property.Affinity;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.DeleteTaskCommand;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class DeleteTaskCommandParserTest {

    private final DeleteTaskCommandParser parser = new DeleteTaskCommandParser();
    private final TaskManagerContext context = new TaskManagerContext();

    @Test
    void test_parse_noArgs() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList(List.of(), List.of()));
        assertEquals(command.getTempIDs().size(), 0);
    }

    @Test
    void test_parse_oneTaskID() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList(List.of("1"), List.of()));
        assertEquals(command.getTempIDs(), List.of(1));
    }

    @Test
    void test_parse_multipleTaskIDs() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList(List.of("3", "111", "333"), List.of()));
        assertEquals(command.getTempIDs(), List.of(3, 111, 333));
    }

    @Test
    void test_parse_invalidTaskID() {
        assertThrows(IllegalArgumentException.class, () -> parse(getArgList(List.of("1.0", "1"), List.of())));
    }

    @Test
    void test_parse_filterPropertyArgs() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList(List.of(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1"))
        )));
        assertEquals(command.getTempIDs().size(), 0);
        assertEquals(command.getFilterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1"))
        ));
    }

    @Test
    void test_parse_complex() throws CommandParserException {
        DeleteTaskCommand command = parse(
                getArgList(
                        List.of(
                                "1", "2"
                        ),
                        List.of(
                                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
                        )
                ));
        assertEquals(command.getFilterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
        ));
    }

    private DeleteTaskCommand parse(ArgumentList argList) throws CommandParserException {
        return (DeleteTaskCommand) parser.parse(context, argList);
    }

    private ArgumentList getArgList(
            List<String> leadingPositionalArguments,
            List<PropertyArgument> filterPropertyArguments
    ) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("delete");
        argList.setLeadingPositionalArguments(leadingPositionalArguments);
        argList.setFilterPropertyArguments(filterPropertyArguments);
        return argList;
    }

}

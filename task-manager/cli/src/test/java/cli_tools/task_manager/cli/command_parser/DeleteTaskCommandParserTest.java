package cli_tools.task_manager.cli.command_parser;

import org.testng.annotations.Test;
import cli_tools.common.core.data.property.Affinity;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.task_manager.cli.Context;
import cli_tools.task_manager.cli.command.DeleteTaskCommand;

import java.util.List;

import static org.testng.Assert.*;

public class DeleteTaskCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList(List.of(), List.of()));
        assertNull(command.getTempIDs());
    }

    @Test
    public void test_parse_oneTaskID() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList(List.of("1"), List.of()));
        assertEquals(command.getTempIDs(), List.of(1));
    }

    @Test
    public void test_parse_multipleTaskIDs() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList(List.of("3", "111", "333"), List.of()));
        assertEquals(command.getTempIDs(), List.of(3, 111, 333));
    }

    @Test
    public void test_parse_invalidTaskID() {
        assertThrows(CommandParserException.class, () -> parse(getArgList(List.of("1.0", "1"), List.of())));
    }

    @Test
    public void test_parse_filterPropertyArgs() throws CommandParserException {
        DeleteTaskCommand command = parse(getArgList(List.of(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1"))
        )));
        assertNull(command.getTempIDs());
        assertEquals(command.getFilterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1"))
        ));
    }

    @Test
    public void test_parse_complex() throws CommandParserException {
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
            List<String> leadingNormalArguments,
            List<PropertyArgument> filterPropertyArguments
    ) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("delete");
        argList.setLeadingNormalArguments(leadingNormalArguments);
        argList.setFilterPropertyArguments(filterPropertyArguments);
        return argList;
    }

    private final DeleteTaskCommandParser parser = new DeleteTaskCommandParser();
    private final Context context = new Context();

}

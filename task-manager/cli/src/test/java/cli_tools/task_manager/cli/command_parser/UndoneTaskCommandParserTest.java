package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.core.data.property.Affinity;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.UndoneTaskCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class UndoneTaskCommandParserTest {

    private final UndoneTaskCommandParser parser = new UndoneTaskCommandParser();
    private final TaskManagerContext context = new TaskManagerContext();

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        UndoneTaskCommand command = parse(getArgList(List.of(), List.of()));
        assertEquals(command.getTempIDs().size(), 0);
    }

    @Test
    public void test_parse_oneTaskID() throws CommandParserException {
        UndoneTaskCommand command = parse(getArgList(List.of("1"), List.of()));
        assertEquals(command.getTempIDs(), List.of(1));
    }

    @Test
    public void test_parse_multipleTaskIDs() throws CommandParserException {
        UndoneTaskCommand command = parse(getArgList(List.of("3", "111", "333"), List.of()));
        assertEquals(command.getTempIDs(), List.of(3, 111, 333));
    }

    @Test
    public void test_parse_invalidTaskID() {
        assertThrows(IllegalArgumentException.class, () -> parse(getArgList(List.of("1asdf", "2"), List.of())));
    }

    @Test
    public void test_parse_filterPropertyArgs() throws CommandParserException {
        UndoneTaskCommand command = parse(getArgList(List.of(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1"))
        )));
        assertEquals(command.getTempIDs().size(), 0);
        assertEquals(command.getFilterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1"))
        ));
    }

    @Test
    public void test_parse_complex() throws CommandParserException {
        UndoneTaskCommand command = parse(
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

    private UndoneTaskCommand parse(ArgumentList argList) throws CommandParserException {
        return (UndoneTaskCommand) parser.parse(context, argList);
    }

    private ArgumentList getArgList(
            @NonNull List<String> leadingPositionalArgs,
            @NonNull List<PropertyArgument> filterPropertyArgs
    ) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("undone");
        argList.setLeadingPositionalArguments(leadingPositionalArgs);
        argList.setFilterPropertyArguments(filterPropertyArgs);
        return argList;
    }
}

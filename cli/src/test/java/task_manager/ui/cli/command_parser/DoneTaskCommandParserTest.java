package task_manager.ui.cli.command_parser;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import task_manager.core.property.Affinity;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.PropertyArgument;
import task_manager.ui.cli.command.DoneTaskCommand;

import java.util.List;

import static org.testng.Assert.*;

public class DoneTaskCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        DoneTaskCommand command = parse(getArgList(List.of(), List.of()));
        assertNull(command.tempIDs());
    }

    @Test
    public void test_parse_oneTaskID() throws CommandParserException {
        DoneTaskCommand command = parse(getArgList(List.of("1"), List.of()));
        assertEquals(command.tempIDs(), List.of(1));
    }

    @Test
    public void test_parse_multipleTaskIDs() throws CommandParserException {
        DoneTaskCommand command = parse(getArgList(List.of("3", "111", "333"), List.of()));
        assertEquals(command.tempIDs(), List.of(3, 111, 333));
    }

    @Test
    public void test_parse_invalidTaskID() {
        assertThrows(CommandParserException.class, () -> parse(getArgList(List.of("1asdf", "2"), List.of())));
    }

    @Test
    public void test_parse_filterPropertyArgs() throws CommandParserException {
        DoneTaskCommand command = parse(getArgList(List.of(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1"))
        )));
        assertNull(command.tempIDs());
        assertEquals(command.filterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1"))
        ));
    }

    @Test
    public void test_parse_complex() throws CommandParserException {
        DoneTaskCommand command = parse(
                getArgList(
                        List.of(
                                "1", "2"
                        ),
                        List.of(
                                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
                        )
                ));
        assertEquals(command.filterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
        ));
    }

    private DoneTaskCommand parse(ArgumentList argList) throws CommandParserException {
        return (DoneTaskCommand) parser.parse(context, argList);
    }

    private ArgumentList getArgList(
            @NonNull List<String> leadingNormalArgs,
            @NonNull List<PropertyArgument> filterPropertyArgs
    ) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("done");
        argList.setLeadingNormalArguments(leadingNormalArgs);
        argList.setFilterPropertyArguments(filterPropertyArgs);
        return argList;
    }

    private final DoneTaskCommandParser parser = new DoneTaskCommandParser();
    private final Context context = new Context();
}

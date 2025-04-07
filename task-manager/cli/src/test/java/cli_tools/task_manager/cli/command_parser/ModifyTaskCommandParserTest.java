package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.command_parser.CommandParserException;
import org.testng.annotations.Test;
import cli_tools.common.core.data.property.Affinity;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.ModifyTaskCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.testng.Assert.*;

public class ModifyTaskCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        ModifyTaskCommand command = parse(getArgList());
        assertEquals(command.getTempIDs().size(), 0);
    }

    @Test
    public void test_parse_oneTaskID() throws CommandParserException {
        ModifyTaskCommand command = parse(getArgList("1"));
        assertEquals(command.getTempIDs(), List.of(1));
    }

    @Test
    public void test_parse_multipleTaskIDs() throws CommandParserException {
        ModifyTaskCommand command = parse(getArgList("3", "111", "333"));
        assertEquals(command.getTempIDs(), List.of(3, 111, 333));
    }

    @Test
    public void test_parse_invalidTaskID() {
        assertThrows(IllegalArgumentException.class, () ->parse(getArgList("1asdf", "2")));
    }

    @Test
    public void test_parse_propertyArgs() throws CommandParserException {
        ModifyTaskCommand command = parse(getArgList(
                List.of("1"),
                List.of(
                        new PropertyArgument(Affinity.NEGATIVE, "prop", "pred", List.of("value1", "value2", "value3"))
                ),
                List.of()
        ));
        assertEquals(command.getTempIDs(), List.of(1));
        assertEquals(command.getModifyPropertyArgs(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", "pred", List.of("value1", "value2", "value3"))));
    }

    @Test
    public void test_parse_complex() throws CommandParserException {
        ModifyTaskCommand command = parse(
                getArgList(
                        List.of(
                                "1", "2"
                        ),
                        List.of(
                                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
                        ),
                        List.of(
                                new PropertyArgument(Affinity.POSITIVE, "prop3", "option1", List.of("value")),
                                new PropertyArgument(Affinity.NEUTRAL, "prop4", null, null)
                        )
                ));
        assertEquals(command.getTempIDs(), List.of(1, 2));
        assertEquals(command.getModifyPropertyArgs(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
        ));
        assertEquals(command.getFilterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop3", "option1", List.of("value")),
                new PropertyArgument(Affinity.NEUTRAL, "prop4", null, null)
        ));
    }

    private ModifyTaskCommand parse(ArgumentList argList) throws CommandParserException {
        return (ModifyTaskCommand) parser.parse(context, argList);
    }

    private ArgumentList getArgList(String... param) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("modify");
        argList.setLeadingPositionalArguments(Arrays.asList(param));
        return argList;
    }

    private ArgumentList getArgList(
            List<String> leadingPositionalArgs,
            List<PropertyArgument> modifyPropertyArgs,
            List<PropertyArgument> filterPropertyArgs
    ) {
        return new ArgumentList(
                "modify",
                leadingPositionalArgs,
                new ArrayList<>(),
                new LinkedHashMap<>(),
                filterPropertyArgs,
                modifyPropertyArgs,
                new ArrayList<>()
        );
    }

    private final ModifyTaskCommandParser parser = new ModifyTaskCommandParser();
    private final TaskManagerContext context = new TaskManagerContext();
}

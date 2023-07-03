package task_manager.ui.cli.command_parser;

import org.testng.annotations.Test;
import task_manager.core.property.Affinity;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.PropertyArgument;
import task_manager.ui.cli.command.ModifyTaskCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class ModifyTaskCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        ModifyTaskCommand command = parse(getArgList());
        assertEquals(command.taskIDs().size(), 0);
    }

    @Test
    public void test_parse_oneTaskID() throws CommandParserException {
        ModifyTaskCommand command = parse(getArgList("1"));
        assertEquals(command.taskIDs(), List.of(1));
    }

    @Test
    public void test_parse_multipleTaskIDs() throws CommandParserException {
        ModifyTaskCommand command = parse(getArgList("3", "111", "333"));
        assertEquals(command.taskIDs(), List.of(3, 111, 333));
    }

    @Test
    public void test_parse_invalidTaskID() {
        assertThrows(CommandParserException.class, () ->parse(getArgList("1", "asdf", "2")));
    }

    @Test
    public void test_parse_propertyArgs() throws CommandParserException {
        ModifyTaskCommand command = parse(getArgList(List.of("1"), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", "pred", List.of("value1", "value2", "value3")))));
        assertEquals(command.taskIDs(), List.of(1));
        assertEquals(command.properties(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", "pred", List.of("value1", "value2", "value3"))));
    }

    private ModifyTaskCommand parse(ArgumentList argList) throws CommandParserException {
        return (ModifyTaskCommand) parser.parse(argList);
    }

    private ArgumentList getArgList(String... param) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("modify");
        argList.setNormalArguments(Arrays.asList(param));
        return argList;
    }

    private ArgumentList getArgList(List<String> normalArgs, List<PropertyArgument> propertyArgs) {
        return new ArgumentList("modify", normalArgs, new LinkedHashMap<>(), propertyArgs, new ArrayList<>());
    }

    final ModifyTaskCommandParser parser = new ModifyTaskCommandParser();
}

package task_manager.ui.cli;

import org.apache.commons.lang3.tuple.Triple;
import org.testng.annotations.Test;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.AddTaskCommand;
import task_manager.property.PropertySpec;
import task_manager.ui.cli.command_parser.AddTaskCommandParser;

import java.util.*;

import static org.testng.Assert.assertEquals;

public class AddTaskCommandParserTest {

    @Test
    public void test_parse_noArgs() {
        AddTaskCommand command = parse(getArgList());
        assertEquals(command.name(), "");
        assertEquals(command.properties().size(), 0);
    }
    
    @Test
    public void test_parse_oneNormalArg() {
        AddTaskCommand command = parse(getArgList("task"));
        assertEquals(command.name(), "task");
        assertEquals(command.properties().size(), 0);

        command = parse(getArgList(""));
        assertEquals(command.name(), "");
        assertEquals(command.properties().size(), 0);
    }

    @Test
    public void test_parse_oneEmptyNormalArg() {
        AddTaskCommand command = parse(getArgList(""));
        assertEquals(command.name(), "");
        assertEquals(command.properties().size(), 0);
    }
    
    @Test
    public void test_parse_multipleNormalArgs() {
        AddTaskCommand command = parse(getArgList("my", "simple", "task"));
        assertEquals(command.name(), "my simple task");
        assertEquals(command.properties().size(), 0);
    }
    
    @Test
    public void test_parse_multipleNormalArgsWithWhitespace() {
        AddTaskCommand command = parse(getArgList("my ", "simple", " task"));
        assertEquals(command.name(), "my  simple  task");
        assertEquals(command.properties().size(), 0);
    }

    @Test
    public void test_parse_onePropertyArg() {
        AddTaskCommand command = parse(getArgList(List.of("task"), List.of(Triple.of(PropertySpec.Affinity.POSITIVE, "prop", List.of("value")))));
        assertEquals(command.name(), "task");
        assertEquals(command.properties(), List.of(Triple.of(PropertySpec.Affinity.POSITIVE, "prop", List.of("value"))));
    }

    @Test
    public void test_parse_onePropertyArgWithoutName() {
        AddTaskCommand command = parse(getArgList(List.of(), List.of(Triple.of(PropertySpec.Affinity.NEUTRAL, "prop", List.of("value")))));
        assertEquals(command.name(), "");
        assertEquals(command.properties(), List.of(Triple.of(PropertySpec.Affinity.NEUTRAL, "prop", List.of("value"))));
    }
    
    @Test
    public void test_parse_onePropertyArgWithMultipleValues() {
        AddTaskCommand command = parse(getArgList(List.of("task"), List.of(Triple.of(PropertySpec.Affinity.NEUTRAL, "prop", List.of("value1", "value2", "value3")))));
        assertEquals(command.name(), "task");
        assertEquals(command.properties(), List.of(Triple.of(PropertySpec.Affinity.NEUTRAL, "prop", List.of("value1", "value2", "value3"))));
    }

    private ArgumentList getArgList(String... normalArgs) {
        return new ArgumentList("add", Arrays.asList(normalArgs), new LinkedHashMap<>(), new ArrayList<>());
    }

    private ArgumentList getArgList(List<String> normalArgs, List<Triple<PropertySpec.Affinity, String, List<String>>> propertyArgs) {
        return new ArgumentList("add", normalArgs, new LinkedHashMap<>(), propertyArgs);
    }
    
    private AddTaskCommand parse(ArgumentList argumentList) {
        return (AddTaskCommand) parser.parse(argumentList);
    }

    final AddTaskCommandParser parser = new AddTaskCommandParser();

}

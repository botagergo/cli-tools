package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.command_parser.CommandParserException;
import org.testng.annotations.Test;
import cli_tools.common.core.data.property.Affinity;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.AddTaskCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class AddTaskCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        AddTaskCommand command = parse(getArgList());
        assertEquals(command.getName(), "");
        assertEquals(command.getModifyPropertyArgs().size(), 0);
    }
    
    @Test
    public void test_parse_oneNormalArg() throws CommandParserException {
        AddTaskCommand command = parse(getArgList("task"));
        assertEquals(command.getName(), "task");
        assertEquals(command.getModifyPropertyArgs().size(), 0);

        command = parse(getArgList(""));
        assertEquals(command.getName(), "");
        assertEquals(command.getModifyPropertyArgs().size(), 0);
    }

    @Test
    public void test_parse_oneEmptyNormalArg() throws CommandParserException {
        AddTaskCommand command = parse(getArgList(""));
        assertEquals(command.getName(), "");
        assertEquals(command.getModifyPropertyArgs().size(), 0);
    }
    
    @Test
    public void test_parse_multipleNormalArgs() throws CommandParserException {
        AddTaskCommand command = parse(getArgList("my", "simple", "task"));
        assertEquals(command.getName(), "my simple task");
        assertEquals(command.getModifyPropertyArgs().size(), 0);
    }
    
    @Test
    public void test_parse_multipleNormalArgsWithWhitespace() throws CommandParserException {
        AddTaskCommand command = parse(getArgList("my ", "simple", " task"));
        assertEquals(command.getName(), "my  simple  task");
        assertEquals(command.getModifyPropertyArgs().size(), 0);
    }

    @Test
    public void test_parse_onePropertyArg() throws CommandParserException {
        AddTaskCommand command = parse(getArgList(List.of("task"), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", "pred", List.of("value")))));
        assertEquals(command.getName(), "task");
        assertEquals(command.getModifyPropertyArgs(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", "pred", List.of("value"))));
    }

    @Test
    public void test_parse_onePropertyArgWithoutName() throws CommandParserException {
        AddTaskCommand command = parse(getArgList(List.of(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value")))));
        assertEquals(command.getName(), "");
        assertEquals(command.getModifyPropertyArgs(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value"))));
    }
    
    @Test
    public void test_parse_onePropertyArgWithMultipleValues() throws CommandParserException {
        AddTaskCommand command = parse(getArgList(List.of("task"), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3")))));
        assertEquals(command.getName(), "task");
        assertEquals(command.getModifyPropertyArgs(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    public void test_parse_complex() throws CommandParserException {
        AddTaskCommand command = parse(
                getArgList(
                        List.of(
                                "1", "2"
                        ),
                        List.of(
                                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
                        )
                ));
        assertEquals(command.getModifyPropertyArgs(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
        ));
    }

    private ArgumentList getArgList(String... normalArgs) {
        return new ArgumentList(
                "add",
                new ArrayList<>(),
                Arrays.asList(normalArgs),
                new LinkedHashMap<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    private ArgumentList getArgList(
            List<String> normalArgs,
            List<PropertyArgument> modifyPropertyArgs
    ) {
        return new ArgumentList(
                "add",
                new ArrayList<>(),
                normalArgs,
                new LinkedHashMap<>(),
                new ArrayList<>(),
                modifyPropertyArgs,
                new ArrayList<>()
        );
    }
    
    private AddTaskCommand parse(ArgumentList argumentList) throws CommandParserException {
        return (AddTaskCommand) parser.parse(context, argumentList);
    }

    private final AddTaskCommandParser parser = new AddTaskCommandParser();
    private final TaskManagerContext context = new TaskManagerContext();

}

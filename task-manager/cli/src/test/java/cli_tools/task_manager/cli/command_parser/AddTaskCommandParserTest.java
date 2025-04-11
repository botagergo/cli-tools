package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.core.data.property.Affinity;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.AddTaskCommand;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class AddTaskCommandParserTest {

    private final AddTaskCommandParser parser = new AddTaskCommandParser();
    private final TaskManagerContext context = new TaskManagerContext();

    @Test
    void test_parse_noArgs() throws CommandParserException {
        AddTaskCommand command = parse(getArgList());
        assertEquals(command.getName(), "");
        assertEquals(command.getModifyPropertyArgs().size(), 0);
    }

    @Test
    void test_parse_onePositionalArg() throws CommandParserException {
        AddTaskCommand command = parse(getArgList("task"));
        assertEquals(command.getName(), "task");
        assertEquals(command.getModifyPropertyArgs().size(), 0);

        command = parse(getArgList(""));
        assertEquals(command.getName(), "");
        assertEquals(command.getModifyPropertyArgs().size(), 0);
    }

    @Test
    void test_parse_oneEmptyPositionalArg() throws CommandParserException {
        AddTaskCommand command = parse(getArgList(""));
        assertEquals(command.getName(), "");
        assertEquals(command.getModifyPropertyArgs().size(), 0);
    }

    @Test
    void test_parse_multiplePositionalArgs() throws CommandParserException {
        AddTaskCommand command = parse(getArgList("my", "simple", "task"));
        assertEquals(command.getName(), "my simple task");
        assertEquals(command.getModifyPropertyArgs().size(), 0);
    }

    @Test
    void test_parse_multiplePositionalArgsWithWhitespace() throws CommandParserException {
        AddTaskCommand command = parse(getArgList("my ", "simple", " task"));
        assertEquals(command.getName(), "my  simple  task");
        assertEquals(command.getModifyPropertyArgs().size(), 0);
    }

    @Test
    void test_parse_onePropertyArg() throws CommandParserException {
        AddTaskCommand command = parse(getArgList(List.of("task"), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", "pred", List.of("value")))));
        assertEquals(command.getName(), "task");
        assertEquals(command.getModifyPropertyArgs(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", "pred", List.of("value"))));
    }

    @Test
    void test_parse_onePropertyArgWithoutName() throws CommandParserException {
        AddTaskCommand command = parse(getArgList(List.of(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value")))));
        assertEquals(command.getName(), "");
        assertEquals(command.getModifyPropertyArgs(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value"))));
    }

    @Test
    void test_parse_onePropertyArgWithMultipleValues() throws CommandParserException {
        AddTaskCommand command = parse(getArgList(List.of("task"), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3")))));
        assertEquals(command.getName(), "task");
        assertEquals(command.getModifyPropertyArgs(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    void test_parse_complex() throws CommandParserException {
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

    private ArgumentList getArgList(String... PositionalArgs) {
        return new ArgumentList(
                "add",
                new ArrayList<>(),
                Arrays.asList(PositionalArgs),
                new LinkedHashMap<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    private ArgumentList getArgList(
            List<String> PositionalArgs,
            List<PropertyArgument> modifyPropertyArgs
    ) {
        return new ArgumentList(
                "add",
                new ArrayList<>(),
                PositionalArgs,
                new LinkedHashMap<>(),
                new ArrayList<>(),
                modifyPropertyArgs,
                new ArrayList<>()
        );
    }

    private AddTaskCommand parse(ArgumentList argumentList) throws CommandParserException {
        return (AddTaskCommand) parser.parse(context, argumentList);
    }

}

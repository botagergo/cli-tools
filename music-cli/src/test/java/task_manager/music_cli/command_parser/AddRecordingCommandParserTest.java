package task_manager.music_cli.command_parser;

import org.testng.annotations.Test;
import task_manager.cli_lib.argument.ArgumentList;
import task_manager.core.property.Affinity;
import task_manager.music_cli.Context;
import task_manager.music_cli.command.AddSongCommand;
import task_manager.cli_lib.argument.PropertyArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class AddRecordingCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        AddSongCommand command = parse(getArgList());
        assertEquals(command.name(), "");
        assertEquals(command.modifyPropertyArgs().size(), 0);
    }
    
    @Test
    public void test_parse_oneNormalArg() throws CommandParserException {
        AddSongCommand command = parse(getArgList("task"));
        assertEquals(command.name(), "task");
        assertEquals(command.modifyPropertyArgs().size(), 0);

        command = parse(getArgList(""));
        assertEquals(command.name(), "");
        assertEquals(command.modifyPropertyArgs().size(), 0);
    }

    @Test
    public void test_parse_oneEmptyNormalArg() throws CommandParserException {
        AddSongCommand command = parse(getArgList(""));
        assertEquals(command.name(), "");
        assertEquals(command.modifyPropertyArgs().size(), 0);
    }
    
    @Test
    public void test_parse_multipleNormalArgs() throws CommandParserException {
        AddSongCommand command = parse(getArgList("my", "simple", "task"));
        assertEquals(command.name(), "my simple task");
        assertEquals(command.modifyPropertyArgs().size(), 0);
    }
    
    @Test
    public void test_parse_multipleNormalArgsWithWhitespace() throws CommandParserException {
        AddSongCommand command = parse(getArgList("my ", "simple", " task"));
        assertEquals(command.name(), "my  simple  task");
        assertEquals(command.modifyPropertyArgs().size(), 0);
    }

    @Test
    public void test_parse_onePropertyArg() throws CommandParserException {
        AddSongCommand command = parse(getArgList(List.of("task"), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", "pred", List.of("value")))));
        assertEquals(command.name(), "task");
        assertEquals(command.modifyPropertyArgs(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", "pred", List.of("value"))));
    }

    @Test
    public void test_parse_onePropertyArgWithoutName() throws CommandParserException {
        AddSongCommand command = parse(getArgList(List.of(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value")))));
        assertEquals(command.name(), "");
        assertEquals(command.modifyPropertyArgs(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value"))));
    }
    
    @Test
    public void test_parse_onePropertyArgWithMultipleValues() throws CommandParserException {
        AddSongCommand command = parse(getArgList(List.of("task"), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3")))));
        assertEquals(command.name(), "task");
        assertEquals(command.modifyPropertyArgs(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    public void test_parse_complex() throws CommandParserException {
        AddSongCommand command = parse(
                getArgList(
                        List.of(
                                "1", "2"
                        ),
                        List.of(
                                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
                        )
                ));
        assertEquals(command.modifyPropertyArgs(), List.of(
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
    
    private AddSongCommand parse(ArgumentList argumentList) throws CommandParserException {
        return (AddSongCommand) parser.parse(context, argumentList);
    }

    private final AddSongCommandParser parser = new AddSongCommandParser();
    private final Context context = new Context();

}

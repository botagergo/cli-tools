package common.music_cli.command_parser;

import org.testng.annotations.Test;
import common.cli.argument.ArgumentList;
import common.core.property.Affinity;
import common.music_cli.Context;
import common.music_cli.command.AddSongCommand;
import common.cli.argument.PropertyArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class AddRecordingCommandParserTest {

    @Test
    void test_parse_noArgs() throws CommandParserException {
        AddSongCommand command = parse(getArgList());
        assertEquals(command.name(), "");
        assertEquals(command.modifyPropertyArgs().size(), 0);
    }
    
    @Test
    void test_parse_onePositionalArg() throws CommandParserException {
        AddSongCommand command = parse(getArgList("task"));
        assertEquals(command.name(), "task");
        assertEquals(command.modifyPropertyArgs().size(), 0);

        command = parse(getArgList(""));
        assertEquals(command.name(), "");
        assertEquals(command.modifyPropertyArgs().size(), 0);
    }

    @Test
    void test_parse_oneEmptyPositionalArg() throws CommandParserException {
        AddSongCommand command = parse(getArgList(""));
        assertEquals(command.name(), "");
        assertEquals(command.modifyPropertyArgs().size(), 0);
    }
    
    @Test
    void test_parse_multiplePositionalArgs() throws CommandParserException {
        AddSongCommand command = parse(getArgList("my", "simple", "task"));
        assertEquals(command.name(), "my simple task");
        assertEquals(command.modifyPropertyArgs().size(), 0);
    }
    
    @Test
    void test_parse_multiplePositionalArgsWithWhitespace() throws CommandParserException {
        AddSongCommand command = parse(getArgList("my ", "simple", " task"));
        assertEquals(command.name(), "my  simple  task");
        assertEquals(command.modifyPropertyArgs().size(), 0);
    }

    @Test
    void test_parse_onePropertyArg() throws CommandParserException {
        AddSongCommand command = parse(getArgList(List.of("task"), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", "pred", List.of("value")))));
        assertEquals(command.name(), "task");
        assertEquals(command.modifyPropertyArgs(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", "pred", List.of("value"))));
    }

    @Test
    void test_parse_onePropertyArgWithoutName() throws CommandParserException {
        AddSongCommand command = parse(getArgList(List.of(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value")))));
        assertEquals(command.name(), "");
        assertEquals(command.modifyPropertyArgs(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value"))));
    }
    
    @Test
    void test_parse_onePropertyArgWithMultipleValues() throws CommandParserException {
        AddSongCommand command = parse(getArgList(List.of("task"), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3")))));
        assertEquals(command.name(), "task");
        assertEquals(command.modifyPropertyArgs(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    void test_parse_complex() throws CommandParserException {
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
    
    private AddSongCommand parse(ArgumentList argumentList) throws CommandParserException {
        return (AddSongCommand) parser.parse(context, argumentList);
    }

    private final AddSongCommandParser parser = new AddSongCommandParser();
    private final Context context = new Context();

}

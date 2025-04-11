package common.music_cli.command_parser;

import org.testng.annotations.Test;
import common.cli.argument.ArgumentList;
import common.core.property.Affinity;
import common.music_cli.Context;
import common.cli.argument.PropertyArgument;
import common.music_cli.command.ModifySongCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.testng.Assert.*;

public class ModifySongCommandParserTest {

    @Test
    void test_parse_noArgs() throws CommandParserException {
        ModifySongCommand command = parse(getArgList());
        assertNull(command.tempIDs());
    }

    @Test
    void test_parse_oneTaskID() throws CommandParserException {
        ModifySongCommand command = parse(getArgList("1"));
        assertEquals(command.tempIDs(), List.of(1));
    }

    @Test
    void test_parse_multipleTaskIDs() throws CommandParserException {
        ModifySongCommand command = parse(getArgList("3", "111", "333"));
        assertEquals(command.tempIDs(), List.of(3, 111, 333));
    }

    @Test
    void test_parse_invalidTaskID() {
        assertThrows(CommandParserException.class, () ->parse(getArgList("1asdf", "2")));
    }

    @Test
    void test_parse_propertyArgs() throws CommandParserException {
        ModifySongCommand command = parse(getArgList(
                List.of("1"),
                List.of(
                        new PropertyArgument(Affinity.NEGATIVE, "prop", "pred", List.of("value1", "value2", "value3"))
                ),
                List.of()
        ));
        assertEquals(command.tempIDs(), List.of(1));
        assertEquals(command.modifyPropertyArgs(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", "pred", List.of("value1", "value2", "value3"))));
    }

    @Test
    void test_parse_complex() throws CommandParserException {
        ModifySongCommand command = parse(
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
        assertEquals(command.tempIDs(), List.of(1, 2));
        assertEquals(command.modifyPropertyArgs(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop1", "option1", List.of("value")),
                new PropertyArgument(Affinity.NEUTRAL, "prop2", null, null)
        ));
        assertEquals(command.filterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop3", "option1", List.of("value")),
                new PropertyArgument(Affinity.NEUTRAL, "prop4", null, null)
        ));
    }

    private ModifySongCommand parse(ArgumentList argList) throws CommandParserException {
        return (ModifySongCommand) parser.parse(context, argList);
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

    private final ModifySongCommandParser parser = new ModifySongCommandParser();
    private final Context context = new Context();
}

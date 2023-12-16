package task_manager.music_cli.command_parser;

import org.testng.annotations.Test;
import task_manager.core.property.Affinity;
import task_manager.music_cli.Context;
import task_manager.music_cli.command.DeleteSongCommand;
import task_manager.cli_lib.argument.ArgumentList;
import task_manager.cli_lib.argument.PropertyArgument;

import java.util.List;

import static org.testng.Assert.*;

public class DeleteSongCommandParserTest {

    @Test
    public void test_parse_noArgs() throws CommandParserException {
        DeleteSongCommand command = parse(getArgList(List.of(), List.of()));
        assertNull(command.tempIDs());
    }

    @Test
    public void test_parse_oneTaskID() throws CommandParserException {
        DeleteSongCommand command = parse(getArgList(List.of("1"), List.of()));
        assertEquals(command.tempIDs(), List.of(1));
    }

    @Test
    public void test_parse_multipleTaskIDs() throws CommandParserException {
        DeleteSongCommand command = parse(getArgList(List.of("3", "111", "333"), List.of()));
        assertEquals(command.tempIDs(), List.of(3, 111, 333));
    }

    @Test
    public void test_parse_invalidTaskID() {
        assertThrows(CommandParserException.class, () -> parse(getArgList(List.of("1.0", "1"), List.of())));
    }

    @Test
    public void test_parse_filterPropertyArgs() throws CommandParserException {
        DeleteSongCommand command = parse(getArgList(List.of(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1"))
        )));
        assertNull(command.tempIDs());
        assertEquals(command.filterPropertyArgs(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1"))
        ));
    }

    @Test
    public void test_parse_complex() throws CommandParserException {
        DeleteSongCommand command = parse(
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

    private DeleteSongCommand parse(ArgumentList argList) throws CommandParserException {
        return (DeleteSongCommand) parser.parse(context, argList);
    }

    private ArgumentList getArgList(
            List<String> leadingNormalArguments,
            List<PropertyArgument> filterPropertyArguments
    ) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("delete");
        argList.setLeadingNormalArguments(leadingNormalArguments);
        argList.setFilterPropertyArguments(filterPropertyArguments);
        return argList;
    }

    private final DeleteSongCommandParser parser = new DeleteSongCommandParser();
    private final Context context = new Context();

}

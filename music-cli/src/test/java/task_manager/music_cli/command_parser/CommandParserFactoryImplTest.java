package task_manager.music_cli.command_parser;

import org.testng.annotations.Test;
import task_manager.cli_lib.argument.ArgumentList;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class CommandParserFactoryImplTest {

    @Test
    public void testAdd() {
        assertTrue(
                commandParserFactory.getParser(getArgList("add")) instanceof AddSongCommandParser);
    }

    @Test
    public void testList() {
        assertTrue(commandParserFactory
                .getParser(getArgList("list")) instanceof ListSongsCommandParser);
    }

    @Test
    public void testUnknown() {
        assertNull(commandParserFactory.getParser(getArgList("unknown")));
        assertNull(commandParserFactory.getParser(getArgList("")));
    }

    private ArgumentList getArgList(String commandName) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName(commandName);
        return argList;
    }

    final CommandParserFactoryImpl commandParserFactory = new CommandParserFactoryImpl();
}

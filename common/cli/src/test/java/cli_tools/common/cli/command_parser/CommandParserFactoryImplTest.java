package cli_tools.common.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.command.Command;
import cli_tools.common.core.repository.ConfigurationRepository;
import lombok.AllArgsConstructor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class CommandParserFactoryImplTest {
    @Mock
    ConfigurationRepository configurationRepository;
    CommandParserFactoryImpl commandParserFactory;

    @BeforeClass
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
        commandParserFactory = new CommandParserFactoryImpl(configurationRepository);
        commandParserFactory.registerParser("add", () -> getTestCommandParser("add"));
        commandParserFactory.registerParser("list", () -> getTestCommandParser("list"));
        commandParserFactory.registerParser("delete", () -> getTestCommandParser("delete"));
        commandParserFactory.registerParser("duplicate", () -> getTestCommandParser("duplicate"));
    }

    @Test
    public void test_found() {
        Mockito.when(configurationRepository.allowCommandPrefix()).thenReturn(false);
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of());
        assertCommandParserName("add", "add");
    }

    @Test
    public void testUnknown() {
        Mockito.when(configurationRepository.allowCommandPrefix()).thenReturn(false);
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of());
        assertNull(commandParserFactory.getParser(getArgList("unknown")));
        assertNull(commandParserFactory.getParser(getArgList("")));
    }

    @Test
    public void testPrefix() {
        Mockito.when(configurationRepository.allowCommandPrefix()).thenReturn(true);
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of());
        assertCommandParserName("lis", "list");
        assertCommandParserName("a", "add");
    }

    @Test
    public void testPrefixMultipleMatches() {
        Mockito.when(configurationRepository.allowCommandPrefix()).thenReturn(true);
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of());
        assertNull(commandParserFactory.getParser(getArgList("d")));
    }

    @Test
    public void testAlias() {
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of("ls", "list", "del", "delete"));
        assertCommandParserName("del", "delete");
        assertCommandParserName("ls", "list");
    }

    private void assertCommandParserName(String command, String name) {
        CommandParser parser = commandParserFactory.getParser(getArgList(command));
        assertEquals(((TestCommandParser) parser).name, name);
    }

    private ArgumentList getArgList(String commandName) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName(commandName);
        return argList;
    }

    private CommandParser getTestCommandParser(String name) {
        return new TestCommandParser(name);
    }
}

@AllArgsConstructor
class TestCommandParser extends CommandParser {

    public final String name;

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        return new Command() {
            @Override
            public void execute(Context context) {

            }
        };
    }

}

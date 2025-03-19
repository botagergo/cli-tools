package cli_tools.task_manager.cli.command_parser;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.core.repository.ConfigurationRepository;

import java.util.Map;

import static org.testng.Assert.*;

public class CommandParserFactoryImplTest {
    @BeforeClass
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
        commandParserFactory = new CommandParserFactoryImpl(configurationRepository);
    }

    @Test
    public void testAdd() {
        Mockito.when(configurationRepository.allowCommandPrefix()).thenReturn(false);
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of());
        assertTrue(
                commandParserFactory.getParser(getArgList("add")) instanceof AddTaskCommandParser);
    }

    @Test
    public void testDone() {
        Mockito.when(configurationRepository.allowCommandPrefix()).thenReturn(false);
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of());
        assertTrue(commandParserFactory
                .getParser(getArgList("done")) instanceof DoneTaskCommandParser);
    }

    @Test
    public void testList() {
        Mockito.when(configurationRepository.allowCommandPrefix()).thenReturn(false);
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of());
        assertTrue(commandParserFactory
                .getParser(getArgList("list")) instanceof ListTasksCommandParser);
    }

    @Test
    public void testUnknown() {
        Mockito.when(configurationRepository.allowCommandPrefix()).thenReturn(false);
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of());
        assertNull(commandParserFactory.getParser(getArgList("del")));
        assertNull(commandParserFactory.getParser(getArgList("unknown")));
        assertNull(commandParserFactory.getParser(getArgList("")));
    }

    @Test
    public void testPrefix() {
        Mockito.when(configurationRepository.allowCommandPrefix()).thenReturn(true);
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of());
        assertTrue(commandParserFactory
                .getParser(getArgList("del")) instanceof DeleteTaskCommandParser);
        assertTrue(commandParserFactory
                .getParser(getArgList("m")) instanceof ModifyTaskCommandParser);
    }

    @Test
    public void testPrefixMultipleMatches() {
        Mockito.when(configurationRepository.allowCommandPrefix()).thenReturn(true);
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of());
        assertNull(commandParserFactory.getParser(getArgList("a")));
    }

    @Test
    public void testAlias() {
        Mockito.when(configurationRepository.commandAliases()).thenReturn(Map.of("ls", "list", "del", "delete"));
        assertTrue(commandParserFactory
                .getParser(getArgList("del")) instanceof DeleteTaskCommandParser);
        assertTrue(commandParserFactory
                .getParser(getArgList("ls")) instanceof ListTasksCommandParser);
    }

    private ArgumentList getArgList(String commandName) {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName(commandName);
        return argList;
    }

    @Mock ConfigurationRepository configurationRepository;
    CommandParserFactoryImpl commandParserFactory;
}

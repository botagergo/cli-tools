package cli_tools.task_manager.cli.command.custom_command;

import cli_tools.common.cli.command.custom_command.CustomCommandDefinition;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CustomCommandParserFactoryImplTest {

    private final CustomCommandParserFactoryImpl factory = new CustomCommandParserFactoryImpl();
    BashCommandDefinition bashCommandDefinition = new BashCommandDefinition("echo", "echo Hello", 1000);

    @Test
    public void test_createParser_withBashCommandDefinition_returnsBashCommandParser() {
        Assert.assertTrue(factory.createParser(bashCommandDefinition) instanceof BashCommandParser);
    }

    @Test
    public void test_createParser_withUnknownCommandDefinition_returnsNull() {
        var unknownCommandDefinitionMock = Mockito.mock(CustomCommandDefinition.class);
        Assert.assertNull(factory.createParser(unknownCommandDefinitionMock));
    }
}
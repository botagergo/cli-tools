package cli_tools.task_manager.cli.command.custom_command;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.argument.OptionArgument;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.command.Command;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.core.data.property.Affinity;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class BashCommandParserTest {

    private final Context context = Mockito.mock(Context.class);
    private final BashCommandParser parser = new BashCommandParser("echo $TASK_0_NAME");

    @Test
    public void test_noArguments() throws CommandParserException {
        Command command = parser.parse(context, new ArgumentList());

        Assert.assertTrue(command instanceof BashCommand);
        BashCommand bashCommand = (BashCommand) command;
        Assert.assertEquals(bashCommand.getBashCommand(), "echo $TASK_0_NAME");
        Assert.assertTrue(bashCommand.getTempIDs().isEmpty());
        Assert.assertTrue(bashCommand.getFilterPropertyArgs().isEmpty());
    }

    @Test(expectedExceptions = CommandParserException.class)
    public void test_trailingArguments_throws() throws CommandParserException {
        ArgumentList argList = new ArgumentList();
        argList.getTrailingNormalArguments().add("arg1");

        BashCommandParser parser = new BashCommandParser("echo $TASK_0_NAME");
        parser.parse(context, argList);
    }

    @Test(expectedExceptions = CommandParserException.class)
    public void test_optionArguments_throws() throws CommandParserException {
        ArgumentList argList = new ArgumentList();
        argList.getOptionArguments().add(new OptionArgument("option", List.of("value")));

        BashCommandParser parser = new BashCommandParser("echo $TASK_0_NAME");
        parser.parse(context, argList);
    }

    @Test(expectedExceptions = CommandParserException.class)
    public void test_propertyArguments_throws() throws CommandParserException {
        ArgumentList argList = new ArgumentList();
        argList.getModifyPropertyArguments().add(
                new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value")));

        BashCommandParser parser = new BashCommandParser("echo $TASK_0_NAME");
        parser.parse(context, argList);
    }

    @Test
    public void testParseValidArguments() throws CommandParserException {
        ArgumentList argList = new ArgumentList();
        argList.setLeadingNormalArguments(List.of("1", "2"));
        argList.setFilterPropertyArguments(List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop", null, List.of("value"))));

        BashCommandParser parser = new BashCommandParser("echo $TASK_0_NAME");
        BashCommand command = (BashCommand) parser.parse(context, argList);

        Assert.assertEquals(command.getTempIDs(), List.of(1, 2));
        Assert.assertEquals(command.getFilterPropertyArgs(), List.of(
                        new PropertyArgument(Affinity.POSITIVE, "prop", null, List.of("value"))));
        Assert.assertEquals(command.getBashCommand(), "echo $TASK_0_NAME");
    }
}
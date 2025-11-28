package cli_tools.common.cli.argument;

import cli_tools.common.core.data.property.Affinity;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class ArgumentListTest {

    @Test
    void test_from_empty() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of());
        Assert.assertNull(argList.getCommandName());
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_noArgs() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_oneTrailingPositionalArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "arg"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments(), List.of("arg"));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_emptyTrailingPositionalArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", ""));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments(), List.of(""));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_multipleTrailingPositionalArgs() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "arg1", "arg2", "arg3"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments(), List.of("arg1", "arg2", "arg3"));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_oneTrailingPositionalArgWithEscapedSemicolon() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop\\:value"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments(), List.of("prop:value"));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_oneLeadingPositionalArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("1", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getLeadingPositionalArguments(), List.of("1"));
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_multipleLeadingPositionalArgs() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("1", "2", "3", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getLeadingPositionalArguments(), List.of("1", "2", "3"));
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_PositionalArgInSingleQuotes() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of(
                "'arg'", "arg1'arg2''arg3'", "''",
                "command",
                "'arg'", "arg1'arg2''arg3'", "''"
        ));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getLeadingPositionalArguments(), List.of("arg", "arg1arg2arg3", ""));
        Assert.assertEquals(argList.getTrailingPositionalArguments(), List.of("arg", "arg1arg2arg3", ""));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_PositionalArgInDoubleQuotes() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of(
                "\"arg\"", "arg1\"arg2\"\"arg3\"", "\"\"",
                "command",
                "\"arg\"", "arg1\"arg2\"\"arg3\"", "\"\""
        ));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getLeadingPositionalArguments(), List.of("arg", "arg1arg2arg3", ""));
        Assert.assertEquals(argList.getTrailingPositionalArguments(), List.of("arg", "arg1arg2arg3", ""));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_mixed() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("1", "prop:value", "2", "command", "arg1", "+arg2", "arg3", "!arg4"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getLeadingPositionalArguments(), List.of("1", "2"));
        Assert.assertEquals(argList.getTrailingPositionalArguments(), List.of("arg1", "arg3"));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "arg2", null, null),
                new PropertyArgument(Affinity.NEGATIVE, "arg4", null, null)));
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_oneModifyPropertyArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop:value"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_oneModifyPropertyArgWithCommas() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop:value1,value2,value3"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneModifyPropertyArgWithCommasAndEmptyValues() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop:,,"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("", "", ""))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneModifyPropertyArgWithCommasAndSomeEmptyValues() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop:value1,,value3"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "", "value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneModifyPropertyArgWithEscapedComma() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop:value1\\,value2"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1,value2"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneModifyPropertyArgWithMultipleEscapedCommas() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop:\\,\\,\\,"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of(",,,"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneModifyPropertyArgEscapedCommaAfterNonEscaped() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop:value1,value2\\,value3"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2,value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneModifyPropertyArgNonEscapedCommaAfterEscaped() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop:value1\\,value2,value3"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1,value2", "value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneNegativeModifyPropertyArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "-prop:value"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneNegatedModifyPropertyArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "!prop:value"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneNegativeModifyPropertyArgWithCommas() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "-prop:value1,value2,value3"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value1", "value2", "value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_onePositiveModifyPropertyArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "+prop:value"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_onePositiveModifyPropertyArgWithCommas() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "+prop:value1,value2,value3"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", null, List.of("value1", "value2", "value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_emptyModifyPropertyArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop:"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of())));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_modifyPropertyArgWithoutValue() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "+prop", "prop.predicate", "-prop"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop", null, null),
                new PropertyArgument(Affinity.NEUTRAL, "prop", "predicate", null),
                new PropertyArgument(Affinity.NEGATIVE, "prop", null, null)
        ));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_multipleModifyPropertyArgs() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "prop1:value1", "+prop2:value2", "-prop3:value3"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1")),
                new PropertyArgument(Affinity.POSITIVE, "prop2", null, List.of("value2")),
                new PropertyArgument(Affinity.NEGATIVE, "prop3", null, List.of("value3"))
        ));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_modifyPropertyArgsWithOption() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(
                List.of("command", "prop1.predicate:value1", "+prop2.option1.option2:value2", "-prop3.:value3", "-.predicate:value4"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", "predicate", List.of("value1")),
                new PropertyArgument(Affinity.POSITIVE, "prop2", "option1", List.of("value2")),
                new PropertyArgument(Affinity.NEGATIVE, "prop3", "", List.of("value3")),
                new PropertyArgument(Affinity.NEGATIVE, "", "predicate", List.of("value4"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneFilterPropertyArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("prop:value", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    void test_from_oneFilterPropertyArgWithCommas() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("prop:value1,value2,value3", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3"))));
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
    }

    @Test
    void test_from_oneFilterPropertyArgWithCommasAndEmptyValues() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("prop:,,", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("", "", ""))));
    }

    @Test
    void test_from_oneFilterPropertyArgWithCommasAndSomeEmptyValues() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("prop:value1,,value3", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "", "value3"))));
    }

    @Test
    void test_from_oneFilterPropertyArgWithEscapedComma() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("prop:value1\\,value2", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1,value2"))));
    }

    @Test
    void test_from_oneFilterPropertyArgWithMultipleEscapedCommas() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("prop:\\,\\,\\,", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of(",,,"))));
    }

    @Test
    void test_from_oneFilterPropertyArgEscapedCommaAfterNonEscaped() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("prop:value1,value2\\,value3", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2,value3"))));
    }

    @Test
    void test_from_oneFilterPropertyArgNonEscapedCommaAfterEscaped() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("prop:value1\\,value2,value3", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1,value2", "value3"))));
    }

    @Test
    void test_from_oneNegativeFilterPropertyArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("-prop:value", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value"))));
    }

    @Test
    void test_from_oneNegatedFilterPropertyArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("!prop:value", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value"))));
    }

    @Test
    void test_from_oneNegativeFilterPropertyArgWithCommas() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("-prop:value1,value2,value3", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    void test_from_onePositiveFilterPropertyArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("+prop:value", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", null, List.of("value"))));
    }

    @Test
    void test_from_onePositiveFilterPropertyArgWithCommas() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("+prop:value1,value2,value3", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    void test_from_emptyFilterPropertyArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("prop:", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of())));
    }

    @Test
    void test_from_filterPropertyArgWithoutValue() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("+prop", "prop.predicate", "-prop", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop", null, null),
                new PropertyArgument(Affinity.NEUTRAL, "prop", "predicate", null),
                new PropertyArgument(Affinity.NEGATIVE, "prop", null, null)
        ));
    }

    @Test
    void test_from_multipleFilterPropertyArgs() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("prop1:value1", "+prop2:value2", "-prop3:value3", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", null, List.of("value1")),
                new PropertyArgument(Affinity.POSITIVE, "prop2", null, List.of("value2")),
                new PropertyArgument(Affinity.NEGATIVE, "prop3", null, List.of("value3"))
        ));
    }

    @Test
    void test_from_filterPropertyArgsWithOption() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(
                List.of("prop1.predicate:value1", "+prop2.option1.option2:value2", "-prop3.:value3", "-.predicate:value4", "command"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", "predicate", List.of("value1")),
                new PropertyArgument(Affinity.POSITIVE, "prop2", "option1", List.of("value2")),
                new PropertyArgument(Affinity.NEGATIVE, "prop3", "", List.of("value3")),
                new PropertyArgument(Affinity.NEGATIVE, "", "predicate", List.of("value4"))));
    }

    @Test
    void test_from_oneOptionArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "/option:value"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments(), List.of(new OptionArgument("option", List.of("value"))));
    }

    @Test
    void test_from_oneOptionArgWithCommas() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "/option:value1,value2"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments(), List.of(new OptionArgument("option", List.of("value1", "value2"))));
    }

    @Test
    void test_from_emptyOptionArg() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "/prop:"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments(), List.of(new OptionArgument("prop", List.of())));
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
    }

    @Test
    void test_from_optionArgWithoutValue() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "/prop"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments(), List.of(new OptionArgument("prop", null)));
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
    }

    @Test
    void test_from_singleSpecialCharacterArgument() throws ArgumentList.ArgumentListException {
        ArgumentList argList = ArgumentList.from(List.of("command", "/", "+", "-", ":"));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingPositionalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments(), List.of(new OptionArgument("", null)));
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "", null, null),
                new PropertyArgument(Affinity.NEGATIVE, "", null, null),
                new PropertyArgument(Affinity.NEUTRAL, "", null, List.of())));
    }

}

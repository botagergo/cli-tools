package task_manager.cli_lib.argument;

import org.apache.commons.lang3.tuple.Pair;
import org.testng.Assert;
import org.testng.annotations.Test;
import task_manager.cli_lib.tokenizer.TokenList;
import task_manager.core.property.Affinity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArgumentListTest {

    @Test
    public void test_from_empty() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of(), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_noArgs() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_oneTrailingNormalArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "arg"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments(), List.of("arg"));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_emptyTrailingNormalArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", ""), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments(), List.of(""));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_multipleTrailingNormalArgs() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "arg1", "arg2", "arg3"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments(), List.of("arg1", "arg2", "arg3"));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_oneTrailingNormalArgWithEscapedSemicolon() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop\\:value"), new HashSet<>(Set.of(Pair.of(1, 5)))));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments(), List.of("prop\\:value"));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_oneLeadingNormalArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("1", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getLeadingNormalArguments(), List.of("1"));
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_multipleLeadingNormalArgs() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("1", "2", "3", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getLeadingNormalArguments(), List.of("1", "2", "3"));
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_multipleSpecialArgs() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "%arg1", "!arg2", "!arg3", "?arg4"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments(), List.of("%arg1", "!arg2", "!arg3", "?arg4"));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_mixed() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("1", "prop:value", "2", "command", "arg1", "+arg2", "arg3", "!arg4"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getLeadingNormalArguments(), List.of("1", "2"));
        Assert.assertEquals(argList.getTrailingNormalArguments(), List.of("arg1", "arg3", "!arg4"));
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.POSITIVE, "arg2", null, null)));
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_oneModifyPropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_oneModifyPropertyArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1,value2,value3"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneModifyPropertyArgWithCommasAndEmptyValues() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:,,"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("", "", ""))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneModifyPropertyArgWithCommasAndSomeEmptyValues() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1,,value3"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "", "value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneModifyPropertyArgWithEscapedComma() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1\\,value2"), new HashSet<>(Set.of(Pair.of(1, 12)))));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1\\,value2"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneModifyPropertyArgWithMultipleEscapedCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:\\,\\,\\,"), new HashSet<>(Set.of(Pair.of(1, 6), Pair.of(1, 8), Pair.of(1, 10)))));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("\\,\\,\\,"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneModifyPropertyArgEscapedCommaAfterNonEscaped() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1,value2\\,value3"), new HashSet<>(Set.of(Pair.of(1, 19)))));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2\\,value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneModifyPropertyArgNonEscapedCommaAfterEscaped() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1\\,value2,value3"), new HashSet<>(Set.of(Pair.of(1, 12)))));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1\\,value2", "value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneNegativeModifyPropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "-prop:value"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneNegativeModifyPropertyArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "-prop:value1,value2,value3"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value1", "value2", "value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_onePositiveModifyPropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "+prop:value"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_onePositiveModifyPropertyArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "+prop:value1,value2,value3"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", null, List.of("value1", "value2", "value3"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_emptyModifyPropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of(""))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_modifyPropertyArgWithoutValue() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "+prop", "prop.option", "-prop"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop", null, null),
                new PropertyArgument(Affinity.NEUTRAL, "prop", "option", null),
                new PropertyArgument(Affinity.NEGATIVE, "prop", null, null)
                ));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_multipleModifyPropertyArgs() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop1:value1", "+prop2:value2", "-prop3:value3"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
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
    public void test_from_modifyPropertyArgsWithOption() {
        ArgumentList argList = ArgumentList.from(new TokenList(
                List.of("command", "prop1.option:value1", "+prop2.option1.option2:value2", "-prop3.:value3", "-.option:value4"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", "option", List.of("value1")),
                new PropertyArgument(Affinity.POSITIVE, "prop2", "option1", List.of("value2")),
                new PropertyArgument(Affinity.NEGATIVE, "prop3", "", List.of("value3")),
                new PropertyArgument(Affinity.NEGATIVE, "", "option", List.of("value4"))));
        Assert.assertEquals(argList.getFilterPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneFilterPropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("prop:value", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value"))));
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
    }

    @Test
    public void test_from_oneFilterPropertyArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("prop:value1,value2,value3", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3"))));
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneFilterPropertyArgWithCommasAndEmptyValues() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("prop:,,", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("", "", ""))));
    }

    @Test
    public void test_from_oneFilterPropertyArgWithCommasAndSomeEmptyValues() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("prop:value1,,value3", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "", "value3"))));
    }

    @Test
    public void test_from_oneFilterPropertyArgWithEscapedComma() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("prop:value1\\,value2", "command"), new HashSet<>(Set.of(Pair.of(0, 12)))));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1\\,value2"))));
    }

    @Test
    public void test_from_oneFilterPropertyArgWithMultipleEscapedCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("prop:\\,\\,\\,", "command"), new HashSet<>(Set.of(Pair.of(0, 6), Pair.of(0, 8), Pair.of(0, 10)))));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("\\,\\,\\,"))));
    }

    @Test
    public void test_from_oneFilterPropertyArgEscapedCommaAfterNonEscaped() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("prop:value1,value2\\,value3", "command"), new HashSet<>(Set.of(Pair.of(0, 19)))));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1", "value2\\,value3"))));
    }

    @Test
    public void test_from_oneFilterPropertyArgNonEscapedCommaAfterEscaped() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("prop:value1\\,value2,value3", "command"), new HashSet<>(Set.of(Pair.of(0, 12)))));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of("value1\\,value2", "value3"))));
    }

    @Test
    public void test_from_oneNegativeFilterPropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("-prop:value", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value"))));
    }

    @Test
    public void test_from_oneNegativeFilterPropertyArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("-prop:value1,value2,value3", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEGATIVE, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    public void test_from_onePositiveFilterPropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("+prop:value", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", null, List.of("value"))));
    }

    @Test
    public void test_from_onePositiveFilterPropertyArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("+prop:value1,value2,value3", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.POSITIVE, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    public void test_from_emptyFilterPropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("prop:", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(new PropertyArgument(Affinity.NEUTRAL, "prop", null, List.of(""))));
    }

    @Test
    public void test_from_filterPropertyArgWithoutValue() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("+prop", "prop.option", "-prop", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "prop", null, null),
                new PropertyArgument(Affinity.NEUTRAL, "prop", "option", null),
                new PropertyArgument(Affinity.NEGATIVE, "prop", null, null)
        ));
    }

    @Test
    public void test_from_multipleFilterPropertyArgs() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("prop1:value1", "+prop2:value2", "-prop3:value3", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
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
    public void test_from_filterPropertyArgsWithOption() {
        ArgumentList argList = ArgumentList.from(new TokenList(
                List.of("prop1.option:value1", "+prop2.option1.option2:value2", "-prop3.:value3", "-.option:value4", "command"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getFilterPropertyArguments(), List.of(
                new PropertyArgument(Affinity.NEUTRAL, "prop1", "option", List.of("value1")),
                new PropertyArgument(Affinity.POSITIVE, "prop2", "option1", List.of("value2")),
                new PropertyArgument(Affinity.NEGATIVE, "prop3", "", List.of("value3")),
                new PropertyArgument(Affinity.NEGATIVE, "", "option", List.of("value4"))));
    }

    @Test
    public void test_from_oneOptionArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", ".option:value"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments(), List.of(new OptionArgument("option", List.of("value"))));
    }

    @Test
    public void test_from_oneOptionArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", ".option:value1,value2"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments(), List.of(new OptionArgument("option", List.of("value1", "value2"))));
    }

    @Test
    public void test_from_emptyOptionArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", ".prop:"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments(), List.of(new OptionArgument("prop", List.of(""))));
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_optionArgWithoutValue() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", ".prop"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments(), List.of(new OptionArgument("prop", null)));
        Assert.assertEquals(argList.getModifyPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_singleSpecialCharacterArgument() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", ".", "+", "-", ":"), new HashSet<>()));
        Assert.assertEquals(argList.getCommandName(), "command");
        Assert.assertEquals(argList.getTrailingNormalArguments().size(), 0);
        Assert.assertEquals(argList.getSpecialArguments().size(), 0);
        Assert.assertEquals(argList.getOptionArguments(), List.of(new OptionArgument("", null)));
        Assert.assertEquals(argList.getModifyPropertyArguments(), List.of(
                new PropertyArgument(Affinity.POSITIVE, "", null, null),
                new PropertyArgument(Affinity.NEGATIVE, "", null, null),
                new PropertyArgument(Affinity.NEUTRAL, "", null, List.of(""))));
    }

}

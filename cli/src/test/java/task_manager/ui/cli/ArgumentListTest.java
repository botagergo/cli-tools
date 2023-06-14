package task_manager.ui.cli;

import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.*;
import task_manager.property.PropertySpec;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.PropertyArgument;
import task_manager.ui.cli.tokenizer.TokenList;

import static org.testng.Assert.*;

import java.util.*;

public class ArgumentListTest {

    @Test
    public void test_from_empty() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of(), new HashSet<>()));
        assertNull(argList.getCommandName());
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_noArgs() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_oneNormalArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "arg"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments(), List.of("arg"));
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_multipleNormalArgs() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "arg1", "arg2", "arg3"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments(), List.of("arg1", "arg2", "arg3"));
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_multipleSpecialArgs() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "+arg1", "!arg2", "+arg3", "!arg4", "?arg5"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments(), List.of("+arg1", "!arg2", "+arg3", "!arg4", "?arg5"));
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_mixed() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "arg1", "+arg2", "arg3", "!arg4"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments(), List.of("arg1", "+arg2", "arg3", "!arg4"));
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_onePropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.NEUTRAL, "prop", null, List.of("value"))));
    }

    @Test
    public void test_from_onePropertyArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1,value2,value3"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.NEUTRAL, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    public void test_from_onePropertyArgWithCommasAndEmptyValues() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:,,"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.NEUTRAL, "prop", null, List.of("", "", ""))));
    }

    @Test
    public void test_from_onePropertyArgWithCommasAndSomeEmptyValues() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1,,value3"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.NEUTRAL, "prop", null, List.of("value1", "", "value3"))));
    }

    @Test
    public void test_from_oneNormalArgWithEscapedSemicolon() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop\\:value"), new HashSet<>(Set.of(Pair.of(1, 5)))));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments(), List.of("prop\\:value"));
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_onePropertyArgWithEscapedComma() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1\\,value2"), new HashSet<>(Set.of(Pair.of(1, 12)))));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.NEUTRAL, "prop", null, List.of("value1\\,value2"))));
    }

    @Test
    public void test_from_onePropertyArgWithMultipleEscapedCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:\\,\\,\\,"), new HashSet<>(Set.of(Pair.of(1, 6), Pair.of(1, 8), Pair.of(1, 10)))));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.NEUTRAL, "prop", null, List.of("\\,\\,\\,"))));
    }

    @Test
    public void test_from_onePropertyArgEscapedCommaAfterNonEscaped() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1,value2\\,value3"), new HashSet<>(Set.of(Pair.of(1, 19)))));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.NEUTRAL, "prop", null, List.of("value1", "value2\\,value3"))));
    }

    @Test
    public void test_from_onePropertyArgNonEscapedCommaAfterEscaped() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1\\,value2,value3"), new HashSet<>(Set.of(Pair.of(1, 12)))));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.NEUTRAL, "prop", null, List.of("value1\\,value2", "value3"))));
    }

    @Test
    public void test_from_oneNegativePropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "-prop:value"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.NEGATIVE, "prop", null, List.of("value"))));
    }

    @Test
    public void test_from_oneNegativePropertyArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "-prop:value1,value2,value3"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.NEGATIVE, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    public void test_from_onePositivePropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "+prop:value"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.POSITIVE, "prop", null, List.of("value"))));
    }

    @Test
    public void test_from_onePositivePropertyArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "+prop:value1,value2,value3"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(new PropertyArgument(PropertySpec.Affinity.POSITIVE, "prop", null, List.of("value1", "value2", "value3"))));
    }

    @Test
    public void test_from_multiplePropertyArgs() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop1:value1", "+prop2:value2", "-prop3:value3"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(
                new PropertyArgument(PropertySpec.Affinity.NEUTRAL, "prop1", null, List.of("value1")),
                new PropertyArgument(PropertySpec.Affinity.POSITIVE, "prop2", null, List.of("value2")),
                new PropertyArgument(PropertySpec.Affinity.NEGATIVE, "prop3", null, List.of("value3"))
        ));
    }

    @Test
    public void test_from_propertyArgsWithPredicate() {
        ArgumentList argList = ArgumentList.from(new TokenList(
                List.of("command", "prop1.equals:value1", "+prop2.contains.equals:value2", "-prop3.:value3", "-.equals:value4"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(
                new PropertyArgument(PropertySpec.Affinity.NEUTRAL, "prop1", "equals", List.of("value1")),
                new PropertyArgument(PropertySpec.Affinity.POSITIVE, "prop2", "contains", List.of("value2")),
                new PropertyArgument(PropertySpec.Affinity.NEGATIVE, "prop3", "", List.of("value3")),
                new PropertyArgument(PropertySpec.Affinity.NEGATIVE, "", "equals", List.of("value4"))
        ));
    }

    @Test
    public void test_from_oneOptionArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", ".option:value"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments().size(), 0);
        assertEquals(argList.getOptionArguments(), List.of(Pair.of("option", List.of("value"))));
    }

    @Test
    public void test_from_oneOptionArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", ".option:value1,value2"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments().size(), 0);
        assertEquals(argList.getOptionArguments(), List.of(Pair.of("option", List.of("value1", "value2"))));
    }

}

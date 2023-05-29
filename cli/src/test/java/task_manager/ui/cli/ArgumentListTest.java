package task_manager.ui.cli;

import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.*;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.SpecialArgument;
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
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments(), new LinkedHashMap<>(Map.of(
                '+', List.of(new SpecialArgument('+', "arg1"), new SpecialArgument('+', "arg3")),
                '!', List.of(new SpecialArgument('!', "arg2"), new SpecialArgument('!', "arg4")),
                '?', List.of(new SpecialArgument('?', "arg5")))));
        assertEquals(argList.getPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_mixed() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "arg1", "+arg2", "arg3", "!arg4"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments(), List.of("arg1", "arg3"));
        assertEquals(argList.getSpecialArguments(), new LinkedHashMap<>(Map.of('+', List.of(new SpecialArgument('+', "arg2")), '!', List.of(new SpecialArgument('!', "arg4")))));
        assertEquals(argList.getPropertyArguments().size(), 0);
    }

    @Test
    public void test_from_onePropertyArg() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(Pair.of("prop", List.of("value"))));
    }

    @Test
    public void test_from_onePropertyArgWithCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1,value2,value3"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(Pair.of("prop", List.of("value1", "value2", "value3"))));
    }

    @Test
    public void test_from_onePropertyArgWithCommasAndEmptyValues() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:,,"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(Pair.of("prop", List.of("", "", ""))));
    }

    @Test
    public void test_from_onePropertyArgWithCommasAndSomeEmptyValues() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1,,value3"), new HashSet<>()));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(Pair.of("prop", List.of("value1", "", "value3"))));
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
        assertEquals(argList.getPropertyArguments(), List.of(Pair.of("prop", List.of("value1\\,value2"))));
    }

    @Test
    public void test_from_onePropertyArgWithMultipleEscapedCommas() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:\\,\\,\\,"), new HashSet<>(Set.of(Pair.of(1, 6), Pair.of(1, 8), Pair.of(1, 10)))));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(Pair.of("prop", List.of("\\,\\,\\,"))));
    }

    @Test
    public void test_from_onePropertyArgEscapedCommaAfterNonEscaped() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1,value2\\,value3"), new HashSet<>(Set.of(Pair.of(1, 19)))));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(Pair.of("prop", List.of("value1", "value2\\,value3"))));
    }

    @Test
    public void test_from_onePropertyArgNonEscapedCommaAfterEscaped() {
        ArgumentList argList = ArgumentList.from(new TokenList(List.of("command", "prop:value1\\,value2,value3"), new HashSet<>(Set.of(Pair.of(1, 12)))));
        assertEquals(argList.getCommandName(), "command");
        assertEquals(argList.getNormalArguments().size(), 0);
        assertEquals(argList.getSpecialArguments().size(), 0);
        assertEquals(argList.getPropertyArguments(), List.of(Pair.of("prop", List.of("value1\\,value2", "value3"))));
    }
}

package task_manager;

import org.testng.annotations.*;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.argument.SpecialArgument;

import static org.testng.Assert.*;
import java.util.List;

public class ArgumentListTest {

    @Test
    public void testEmpty() {
        ArgumentList argList = ArgumentList.from(List.of());
        assertNull(argList.commandName);
        assertEquals(argList.normalArguments.size(), 0);
        assertEquals(argList.specialArguments.size(), 0);
    }

    @Test
    public void testSingle() {
        ArgumentList argList = ArgumentList.from(List.of("command"));
        assertEquals(argList.commandName, "command");
        assertEquals(argList.normalArguments.size(), 0);
        assertEquals(argList.specialArguments.size(), 0);
    }

    @Test
    public void testMultipleNormalArgs() {
        ArgumentList argList = ArgumentList.from(List.of("command", "arg1", "arg2", "arg3"));
        assertEquals(argList.commandName, "command");
        assertEquals(argList.normalArguments.size(), 3);
        assertEquals(argList.normalArguments.get(0), "arg1");
        assertEquals(argList.normalArguments.get(1), "arg2");
        assertEquals(argList.normalArguments.get(2), "arg3");
        assertEquals(argList.specialArguments.size(), 0);
    }

    @Test
    public void testMultipleSpecialArgs() {
        ArgumentList argList = ArgumentList.from(List.of("command", "+arg1", "!arg2", "+arg3", "!arg4", "?arg5"));
        assertEquals(argList.commandName, "command");
        assertEquals(argList.normalArguments.size(), 0);

        assertEquals(argList.specialArguments.size(), 3);

        List<SpecialArgument> plusSpecialArgs = argList.specialArguments.get('+');
        assertEquals(plusSpecialArgs.size(), 2);
        assertEquals(plusSpecialArgs.get(0).type, '+');
        assertEquals(plusSpecialArgs.get(0).value, "arg1");
        assertEquals(plusSpecialArgs.get(1).type, '+');
        assertEquals(plusSpecialArgs.get(1).value, "arg3");

        List<SpecialArgument> exclSpecialArgs = argList.specialArguments.get('!');
        assertEquals(exclSpecialArgs.size(), 2);
        assertEquals(exclSpecialArgs.get(0).type, '!');
        assertEquals(exclSpecialArgs.get(0).value, "arg2");
        assertEquals(exclSpecialArgs.get(1).type, '!');
        assertEquals(exclSpecialArgs.get(1).value, "arg4");

        List<SpecialArgument> questionSpecialArgs = argList.specialArguments.get('?');
        assertEquals(questionSpecialArgs.size(), 1);
        assertEquals(questionSpecialArgs.get(0).type, '?');
        assertEquals(questionSpecialArgs.get(0).value, "arg5");
    }

    @Test
    public void testMixed() {
        ArgumentList argList = ArgumentList.from(List.of("command", "arg1", "+arg2", "arg3", "!arg4"));
        assertEquals(argList.commandName, "command");
        assertEquals(argList.normalArguments.size(), 2);
        assertEquals(argList.normalArguments.get(0), "arg1");
        assertEquals(argList.normalArguments.get(1), "arg3");

        assertEquals(argList.specialArguments.size(), 2);

        List<SpecialArgument> plusSpecialArgs = argList.specialArguments.get('+');
        assertEquals(plusSpecialArgs.size(), 1);
        assertEquals(plusSpecialArgs.get(0).type, '+');
        assertEquals(plusSpecialArgs.get(0).value, "arg2");

        List<SpecialArgument> exclSpecialArgs = argList.specialArguments.get('!');
        assertEquals(exclSpecialArgs.size(), 1);
        assertEquals(exclSpecialArgs.get(0).type, '!');
        assertEquals(exclSpecialArgs.get(0).value, "arg4");
    }
}

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
    public void testMixed() {
        ArgumentList argList =
                ArgumentList.from(List.of("command", "arg1", "+arg2", "arg3", "!arg4"));
        assertEquals(argList.commandName, "command");
        assertEquals(argList.normalArguments.size(), 2);
        assertEquals(argList.normalArguments.get(0), "arg1");
        assertEquals(argList.normalArguments.get(1), "arg3");
        assertEquals(argList.specialArguments.size(), 2);
        assertEquals(argList.specialArguments.get(0), new SpecialArgument('+', "arg2"));
        assertEquals(argList.specialArguments.get(1), new SpecialArgument('!', "arg4"));
    }
}

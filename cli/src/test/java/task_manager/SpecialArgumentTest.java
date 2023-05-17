package task_manager;

import org.testng.annotations.*;

import task_manager.ui.cli.argument.NotASpecialArgumentException;
import task_manager.ui.cli.argument.SpecialArgument;

import static org.testng.Assert.*;

public class SpecialArgumentTest {

    @Test
    public void testEmpty() {
        assertFalse(SpecialArgument.isSpecialArgument(""));
        assertThrows(NotASpecialArgumentException.class, () -> SpecialArgument.from(""));
    }

    @Test
    public void testBlank() {
        assertFalse(SpecialArgument.isSpecialArgument("   "));
        assertThrows(NotASpecialArgumentException.class, () -> SpecialArgument.from("   "));
    }

    @Test
    public void testNotSpecialArgument() {
        assertFalse(SpecialArgument.isSpecialArgument("Parg"));
        assertThrows(NotASpecialArgumentException.class, () -> SpecialArgument.from("Parg"));
    }

    @Test
    public void testSpecialArgument() throws NotASpecialArgumentException {
        assertTrue(SpecialArgument.isSpecialArgument("+arg"));

        SpecialArgument specArg = SpecialArgument.from("+arg");
        assertEquals(specArg.type, '+');
        assertEquals(specArg.value, "arg");
    }

    @Test
    public void testNotSpecialArgumentChar() {
        assertFalse(SpecialArgument.isSpecialArgumentChar('P'));
    }

    @Test
    public void testSpecialArgumentChar() {
        assertTrue(SpecialArgument.isSpecialArgumentChar('+'));
    }
}

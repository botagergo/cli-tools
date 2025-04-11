package cli_tools.common.cli.argument;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SpecialArgumentTest {

    @Test
    void testEmpty() {
        Assert.assertFalse(SpecialArgument.isSpecialArgument(""));
        Assert.assertThrows(NotASpecialArgumentException.class, () -> SpecialArgument.from(""));
    }

    @Test
    void testBlank() {
        Assert.assertFalse(SpecialArgument.isSpecialArgument("   "));
        Assert.assertThrows(NotASpecialArgumentException.class, () -> SpecialArgument.from("   "));
    }

    @Test
    void testNotSpecialArgument() {
        Assert.assertFalse(SpecialArgument.isSpecialArgument("Parg"));
        Assert.assertThrows(NotASpecialArgumentException.class, () -> SpecialArgument.from("Parg"));
    }

    @Test
    void testSpecialArgument() throws NotASpecialArgumentException {
        Assert.assertTrue(SpecialArgument.isSpecialArgument("+arg"));

        SpecialArgument specArg = SpecialArgument.from("+arg");
        Assert.assertEquals(specArg.type(), '+');
        Assert.assertEquals(specArg.value(), "arg");
    }

    @Test
    void testNotSpecialArgumentChar() {
        Assert.assertFalse(SpecialArgument.isSpecialArgumentChar('P'));
    }

    @Test
    void testSpecialArgumentChar() {
        Assert.assertTrue(SpecialArgument.isSpecialArgumentChar('+'));
    }
}

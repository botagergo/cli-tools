package cli_tools.common.cli.argument;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SpecialArgumentTest {

    @Test
    public void testEmpty() {
        Assert.assertFalse(SpecialArgument.isSpecialArgument(""));
        Assert.assertThrows(NotASpecialArgumentException.class, () -> SpecialArgument.from(""));
    }

    @Test
    public void testBlank() {
        Assert.assertFalse(SpecialArgument.isSpecialArgument("   "));
        Assert.assertThrows(NotASpecialArgumentException.class, () -> SpecialArgument.from("   "));
    }

    @Test
    public void testNotSpecialArgument() {
        Assert.assertFalse(SpecialArgument.isSpecialArgument("Parg"));
        Assert.assertThrows(NotASpecialArgumentException.class, () -> SpecialArgument.from("Parg"));
    }

    @Test
    public void testSpecialArgument() throws NotASpecialArgumentException {
        Assert.assertTrue(SpecialArgument.isSpecialArgument("+arg"));

        SpecialArgument specArg = SpecialArgument.from("+arg");
        Assert.assertEquals(specArg.type(), '+');
        Assert.assertEquals(specArg.value(), "arg");
    }

    @Test
    public void testNotSpecialArgumentChar() {
        Assert.assertFalse(SpecialArgument.isSpecialArgumentChar('P'));
    }

    @Test
    public void testSpecialArgumentChar() {
        Assert.assertTrue(SpecialArgument.isSpecialArgumentChar('+'));
    }
}

package cli_tools.common.cli.argument;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SpecialArgumentTest {
    @Test
    void testNotSpecialArgumentChar() {
        Assert.assertFalse(SpecialArgument.isSpecialArgumentChar('P'));
    }

    @Test
    void testSpecialArgumentChar() {
        Assert.assertTrue(SpecialArgument.isSpecialArgumentChar('$'));
    }
}

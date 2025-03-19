package common.music_cli;

import org.testng.Assert;
import org.testng.annotations.Test;
import common.music_cli.command_parser.CommandParserException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class UtilTest {

    @Test
    public void test_parseTaskID_valid() throws CommandParserException {
        Assert.assertEquals(Util.parseID("1"), 1);
        Assert.assertEquals(Util.parseID("10"), 10);
        Assert.assertEquals(Util.parseID("33"), 33);
        Assert.assertEquals(Util.parseID("999999999"), 999999999);
    }

    @Test
    public void test_parseTaskID_invalid() {
        assertThrows(CommandParserException.class, () -> Util.parseID(""));
        assertThrows(CommandParserException.class, () -> Util.parseID("  "));
        assertThrows(CommandParserException.class, () -> Util.parseID("id"));
        assertThrows(CommandParserException.class, () -> Util.parseID("0"));
        assertThrows(CommandParserException.class, () -> Util.parseID("-33"));
        assertThrows(CommandParserException.class, () -> Util.parseID("1.3"));
        assertThrows(CommandParserException.class, () -> Util.parseID("10e+23"));
    }

}

package cli_tools.common.cli;

import org.testng.annotations.Test;
import cli_tools.common.cli.command_parser.CommandParserException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class UtilTest {

    @Test
    public void test_parseTempId_valid() {
        assertEquals(Util.parseTempId("1"), 1);
        assertEquals(Util.parseTempId("10"), 10);
        assertEquals(Util.parseTempId("33"), 33);
        assertEquals(Util.parseTempId("999999999"), 999999999);
    }

    @Test
    public void test_parseTempId_invalid() {
        assertThrows(IllegalArgumentException.class, () -> Util.parseTempId(""));
        assertThrows(IllegalArgumentException.class, () -> Util.parseTempId("  "));
        assertThrows(IllegalArgumentException.class, () -> Util.parseTempId("id"));
        assertThrows(IllegalArgumentException.class, () -> Util.parseTempId("0"));
        assertThrows(IllegalArgumentException.class, () -> Util.parseTempId("-33"));
        assertThrows(IllegalArgumentException.class, () -> Util.parseTempId("1.3"));
        assertThrows(IllegalArgumentException.class, () -> Util.parseTempId("10e+23"));
    }

}

package cli_tools.task_manager.cli;

import cli_tools.task_manager.cli.Util;
import org.testng.annotations.Test;
import cli_tools.task_manager.cli.command_parser.CommandParserException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class UtilTest {

    @Test
    public void test_parseTaskID_valid() throws CommandParserException {
        assertEquals(Util.parseTaskID("1"), 1);
        assertEquals(Util.parseTaskID("10"), 10);
        assertEquals(Util.parseTaskID("33"), 33);
        assertEquals(Util.parseTaskID("999999999"), 999999999);
    }

    @Test
    public void test_parseTaskID_invalid() {
        assertThrows(CommandParserException.class, () -> Util.parseTaskID(""));
        assertThrows(CommandParserException.class, () -> Util.parseTaskID("  "));
        assertThrows(CommandParserException.class, () -> Util.parseTaskID("id"));
        assertThrows(CommandParserException.class, () -> Util.parseTaskID("0"));
        assertThrows(CommandParserException.class, () -> Util.parseTaskID("-33"));
        assertThrows(CommandParserException.class, () -> Util.parseTaskID("1.3"));
        assertThrows(CommandParserException.class, () -> Util.parseTaskID("10e+23"));
    }

}

package task_manager.ui.cli;

import org.testng.annotations.Test;
import task_manager.ui.cli.command_parser.CommandParserException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class UtilTest {

    @Test
    public void test_parseTaskID_valid() throws CommandParserException {
        assertEquals(task_manager.ui.cli.Util.parseTaskID("1"), 1);
        assertEquals(task_manager.ui.cli.Util.parseTaskID("10"), 10);
        assertEquals(task_manager.ui.cli.Util.parseTaskID("33"), 33);
        assertEquals(task_manager.ui.cli.Util.parseTaskID("999999999"), 999999999);
    }

    @Test
    public void test_parseTaskID_invalid() {
        assertThrows(CommandParserException.class, () -> task_manager.ui.cli.Util.parseTaskID(""));
        assertThrows(CommandParserException.class, () -> task_manager.ui.cli.Util.parseTaskID("  "));
        assertThrows(CommandParserException.class, () -> task_manager.ui.cli.Util.parseTaskID("id"));
        assertThrows(CommandParserException.class, () -> task_manager.ui.cli.Util.parseTaskID("0"));
        assertThrows(CommandParserException.class, () -> task_manager.ui.cli.Util.parseTaskID("-33"));
        assertThrows(CommandParserException.class, () -> task_manager.ui.cli.Util.parseTaskID("1.3"));
        assertThrows(CommandParserException.class, () -> Util.parseTaskID("10e+23"));
    }

}

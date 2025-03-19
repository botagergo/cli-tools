package common.music_cli.command_parser;

import org.testng.annotations.Test;
import common.cli.argument.ArgumentList;
import common.music_cli.Context;
import common.music_cli.command.ClearCommand;

import static org.testng.Assert.assertNotNull;

public class ClearCommandParserTest {

    @Test
    public void test_parse_noArgs() {
        ClearCommand cmd = (ClearCommand) parser.parse(context, getArgList());
        assertNotNull(cmd);
    }

    private ArgumentList getArgList() {
        ArgumentList argList = new ArgumentList();
        argList.setCommandName("clear");
        return argList;
    }

    private final ClearCommandParser parser = new ClearCommandParser();
    private final Context context = new Context();

}

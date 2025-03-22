package cli_tools.common.cli.command;

import cli_tools.common.cli.Context;

public class CommandExecutor {

    public void execute(Context context, Command command) {
        command.execute(context);
    }

}

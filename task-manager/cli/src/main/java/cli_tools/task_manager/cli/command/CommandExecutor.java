package cli_tools.task_manager.cli.command;

import cli_tools.task_manager.cli.Context;

public class CommandExecutor {

    public void execute(Context context, Command command) {
        command.execute(context);
    }

}

package task_manager.ui.cli.command;

import task_manager.ui.cli.Context;

public class CommandExecutor {

    public void execute(Context context, Command command) {
        command.execute(context);
    }

}

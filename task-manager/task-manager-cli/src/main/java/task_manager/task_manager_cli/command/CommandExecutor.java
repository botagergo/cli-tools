package task_manager.task_manager_cli.command;

import task_manager.task_manager_cli.Context;

public class CommandExecutor {

    public void execute(Context context, Command command) {
        command.execute(context);
    }

}

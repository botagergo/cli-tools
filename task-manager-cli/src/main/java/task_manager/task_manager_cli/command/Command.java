package task_manager.task_manager_cli.command;

import task_manager.task_manager_cli.Context;

public interface Command {
    void execute(Context context);
}

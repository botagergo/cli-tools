package task_manager.ui.cli.command;

import task_manager.ui.cli.Context;

public interface Command {
    void execute(Context context);
}

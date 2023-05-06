package task_manager.api.command;

import task_manager.api.Context;

public interface Command {
    public void execute(Context context);
}

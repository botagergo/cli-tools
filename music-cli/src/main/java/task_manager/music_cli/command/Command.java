package task_manager.music_cli.command;

import task_manager.music_cli.Context;

public interface Command {
    void execute(Context context);
}

package task_manager.music_cli.command;

import task_manager.music_cli.Context;

public class CommandExecutor {

    public void execute(Context context, Command command) {
        command.execute(context);
    }

}

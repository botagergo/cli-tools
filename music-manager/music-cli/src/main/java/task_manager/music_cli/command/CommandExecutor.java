package common.music_cli.command;

import common.music_cli.Context;

public class CommandExecutor {

    public void execute(Context context, Command command) {
        command.execute(context);
    }

}

package task_manager.ui.cli;

import task_manager.api.command.Command;

public class Executor {
    public void execute(Command command) {
        command.execute();
    }

    public boolean shouldExit() {
        return false;
    }
}

package task_manager.api;

import com.google.inject.Inject;
import task_manager.api.command.Command;

public class Executor {
    public void execute(Command command) {
        command.execute(context);
    }

    public boolean shouldExit() {
        return false;
    }

    public Context getContext() {
        return context;
    }

    @Inject
    private Context context;

}

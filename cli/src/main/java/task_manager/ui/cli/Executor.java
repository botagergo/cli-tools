package task_manager.ui.cli;

import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import task_manager.ui.cli.command.Command;

public class Executor {

    public void execute(Command command) throws Exception {
        command.execute(context);
    }

    public boolean shouldExit() {
        return false;
    }

    @Getter @Setter @Inject
    private Context context;

}

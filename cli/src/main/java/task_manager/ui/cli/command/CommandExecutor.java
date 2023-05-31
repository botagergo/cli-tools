package task_manager.ui.cli.command;

import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import task_manager.ui.cli.Context;

public class CommandExecutor {

    public void execute(Command command) {
        command.execute(context);
    }

    @Getter @Setter @Inject
    private Context context;

}

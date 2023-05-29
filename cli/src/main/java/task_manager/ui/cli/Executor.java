package task_manager.ui.cli;

import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import task_manager.ui.cli.command.Command;

public class Executor {

    public void execute(Command command) {
        command.execute(context);
    }

    @Getter @Setter @Inject
    private Context context;

}

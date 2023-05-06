package task_manager.ui.cli.command;

import task_manager.ui.cli.Context;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DeleteTaskCommand implements Command {

    public DeleteTaskCommand(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void execute(Context context) throws Exception {
        log.traceEntry();
        context.getTaskUseCase().deleteTask(uuid);
    }

    public final UUID uuid;

}

package task_manager.ui.cli.command;

import lombok.extern.log4j.Log4j2;
import task_manager.data.Task;
import task_manager.ui.cli.Context;

@Log4j2
public class ModifyTaskCommand implements Command {

    public ModifyTaskCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(Context context) throws Exception {
        log.info("execute");
        context.getTaskUseCase().modifyTask(task);
    }

    public final Task task;

}

package task_manager.music_cli.command;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.music_cli.Context;

@Log4j2
public class ClearCommand implements Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            context.getTaskUseCase().deleteAllTasks();
            context.getLabelUseCase().deleteAllLabels("tag");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

}

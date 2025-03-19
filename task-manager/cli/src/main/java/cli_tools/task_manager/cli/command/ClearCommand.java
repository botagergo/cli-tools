package cli_tools.task_manager.cli.command;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import cli_tools.task_manager.cli.Context;

@Log4j2
public class ClearCommand extends Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            context.getTaskService().deleteAllTasks();
            context.getLabelService().deleteAllLabels("tag");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

}

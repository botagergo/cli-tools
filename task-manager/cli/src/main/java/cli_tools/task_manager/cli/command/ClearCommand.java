package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.command.Command;
import cli_tools.common.core.util.Print;
import cli_tools.task_manager.cli.TaskManagerContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Log4j2
public class ClearCommand extends Command {

    @Override
    public void execute(cli_tools.common.cli.Context context) {
        log.traceEntry();

        try {
            ((TaskManagerContext) context).getTaskService().deleteAllTasks();
            context.getTempIdManager().deleteAll();
            context.getLabelService().deleteAllLabels("tag");
        } catch (Exception e) {
            Print.printError(e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

}

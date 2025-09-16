package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.cli.command.Command;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.core.util.Print;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.backend.task.Task;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.UUID;

@Log4j2
@Getter
@Setter
public final class DoneTaskCommand extends Command {

    private List<@NonNull Integer> tempIDs;
    private List<@NonNull PropertyArgument> filterPropertyArgs;

    @Override
    public void execute(cli_tools.common.cli.Context context) {
        log.traceEntry();

        TaskManagerContext taskManagerContext = (TaskManagerContext) context;

        try {
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(taskManagerContext, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(taskManagerContext, filterPropertyArgs);

            List<Task> tasks = taskManagerContext.getTaskService().getTasks(
                    filterPropertySpecs, null, null, taskUUIDs, true
            );

            tasks = CommandUtil.confirmAndGetTasksToChange(taskManagerContext, tasks, tempIDs, filterPropertySpecs, CommandUtil.ChangeType.DONE);
            if (tasks == null || tasks.isEmpty()) {
                return;
            }

            for (Task task : tasks) {
                Task doneTask = taskManagerContext.getTaskService().doneTask(task.getUUID());
                if (tasks.size() == 1) {
                    int tempID = context.getTempIdManager().getOrCreateID(doneTask.getUUID());
                    context.setPrevTempId(tempID);
                }
            }
        } catch (Exception e) {
            Print.printError(e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

}

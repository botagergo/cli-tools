package cli_tools.task_manager.cli.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.task_manager.task.Task;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.task_manager.cli.Context;

import java.util.List;
import java.util.UUID;

@Log4j2
@Getter
@Setter
public final class DeleteTaskCommand extends Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(context, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(context, filterPropertyArgs);

            List<Task> tasks = context.getTaskService().getTasks(
                    filterPropertySpecs, null, null, taskUUIDs);

            List<Task> tasksToDelete = CommandUtil.confirmAndGetTasksToChange(context, tasks, tempIDs, filterPropertySpecs, CommandUtil.ChangeType.DELETE);
            if (tasksToDelete == null || tasksToDelete.isEmpty()) {
                return;
            }

            List<UUID> uuids = tasksToDelete.stream().map(Task::getUUID).toList();

            for (UUID uuid : uuids) {
                context.getTempIDMappingService().delete(uuid);
                context.getTaskService().deleteTask(uuid);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    private List<@NonNull PropertyArgument> filterPropertyArgs;
    private List<@NonNull Integer> tempIDs;

}

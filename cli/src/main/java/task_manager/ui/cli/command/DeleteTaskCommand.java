package task_manager.ui.cli.command;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.core.data.Task;
import task_manager.core.property.FilterPropertySpec;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.PropertyArgument;

import java.util.List;
import java.util.UUID;

@Log4j2
public record DeleteTaskCommand(
        List<@NonNull Integer> tempIDs,
        List<@NonNull PropertyArgument> filterPropertyArgs
) implements Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            List<UUID> taskUUIDs = CommandUtil.getUUIDsFromTempIDs(context, tempIDs);
            List<FilterPropertySpec> filterPropertySpecs = CommandUtil.getFilterPropertySpecs(context, filterPropertyArgs);

            List<Task> tasks = context.getTaskUseCase().getTasks(
                    null, filterPropertySpecs, null, null, taskUUIDs);

            List<Task> tasksToDelete = CommandUtil.confirmAndGetTasksToChange(context, tasks, tempIDs, filterPropertySpecs, CommandUtil.ChangeType.DELETE);
            if (tasksToDelete == null || tasksToDelete.isEmpty()) {
                return;
            }

            List<UUID> uuids = tasksToDelete.stream().map(Task::getUUID).toList();

            for (UUID uuid : uuids) {
                context.getTempIDMappingRepository().delete(uuid);
                boolean result = context.getTaskUseCase().deleteTask(uuid);
                if (!result) {
                    System.out.println("ERROR: Task with uuid '" + uuid + "' not found");
                    log.info("task with uuid '" + uuid + "' not found");
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

}

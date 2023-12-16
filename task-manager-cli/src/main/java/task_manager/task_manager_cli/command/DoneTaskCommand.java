package task_manager.task_manager_cli.command;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.core.data.Task;
import task_manager.core.property.FilterPropertySpec;
import task_manager.cli_lib.argument.PropertyArgument;
import task_manager.task_manager_cli.Context;

import java.util.List;
import java.util.UUID;

@Log4j2
public record DoneTaskCommand(
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
                    null, filterPropertySpecs, null, null, taskUUIDs
            );

            tasks = CommandUtil.confirmAndGetTasksToChange(context, tasks, tempIDs, filterPropertySpecs, CommandUtil.ChangeType.DONE);
            if (tasks == null || tasks.isEmpty()) {
                return;
            }

            for (Task task : tasks) {
                context.getTempIDMappingUseCase().delete(task.getUUID());
                context.getPropertyManager().setProperty(task, "done", true);
                Task updatedTask = context.getTaskUseCase().modifyTask(task);

                if (tasks.size() == 1) {
                    int tempID = context.getTempIDMappingUseCase().getOrCreateID(updatedTask.getUUID());
                    context.setPrevTaskID(tempID);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

}

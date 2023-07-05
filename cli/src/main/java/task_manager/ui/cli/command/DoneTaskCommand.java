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
            for (Task task : tasks) {
                context.getTempIDMappingRepository().delete(task.getUUID());
                context.getPropertyManager().setProperty(task, "done", true);
                Task updatedTask = context.getTaskUseCase().modifyTask(task);

                if (updatedTask == null) {
                    System.out.println("ERROR: Task with uuid '" + task.getUUID() + "' not found");
                    log.info("task with uuid '" + task.getUUID() + "' not found");
                } else if (tasks.size() == 1) {
                    int tempID = context.getTempIDMappingRepository().getOrCreateID(updatedTask.getUUID());
                    context.setPrevTaskID(tempID);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

}

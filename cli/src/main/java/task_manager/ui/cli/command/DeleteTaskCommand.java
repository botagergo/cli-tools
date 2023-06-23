package task_manager.ui.cli.command;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.data.Task;
import task_manager.ui.cli.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
public record DeleteTaskCommand(List<Integer> taskIDs) implements Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        if (taskIDs.isEmpty()) {
            System.out.println("No selector was specified");
            return;
        }

        try {
            List<Task> tasks = new ArrayList<>();

            for (int taskID : taskIDs) {
                UUID uuid = context.getTempIDMappingRepository().getUUID(taskID);
                tasks.add(context.getTaskUseCase().getTask(uuid));
            }

            for (Task task : tasks) {
                context.getTempIDMappingRepository().delete(task.getUUID());
                boolean result = context.getTaskUseCase().deleteTask(
                        context.getPropertyManager().getProperty("uuid", task).getUuid());
                if (!result) {
                    System.out.println("Failed to delete task '" + task.getUUID() + "'");
                    log.info("failed to delete task '" + task.getUUID() + "'");
                }
            }
        } catch (IOException e) {
            System.out.println("An IO error has occurred: " + e.getMessage());
            System.out.println("Check the logs for details.");
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("{}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

}

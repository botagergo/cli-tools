package task_manager.ui.cli.command;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import task_manager.data.Task;
import task_manager.ui.cli.Context;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Log4j2
public record DeleteTaskCommand(String query) implements Command {

    @Override
    public void execute(Context context) {
        log.traceEntry();

        try {
            List<Task> tasks;

            try {
                int taskID = Integer.parseInt(query);
                UUID uuid = context.getTempIDMappingRepository().getUUID(taskID);
                tasks = List.of(context.getTaskUseCase().getTask(uuid));
            } catch (Exception e) {
                tasks = context.getTaskUseCase().getTasks(query, null);
            }

            if (tasks.size() == 0) {
                System.out.println("No task matches the string '" + query + "'");
                log.info("no task matches the string '{}'", query);
            } else if (tasks.size() > 1) {
                System.out.println("Multiple tasks match the string '" + query + "'");
                log.info("multiple tasks match the string '{}'", query);
            } else {
                Task task = tasks.get(0);
                context.getTempIDMappingRepository().delete(task.getUUID());
                boolean result = context.getTaskUseCase().deleteTask(
                        context.getPropertyManager().getProperty(task, "uuid").getUuid());
                if (result) {
                    System.out.println("Task deleted successfully");
                    log.info("deleted task: {}", task);
                } else {
                    System.out.println("No task matches the string '" + query + "'");
                    log.info("failed to delete task: {}",
                            context.getPropertyManager().getProperty(task, "uuid"));
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

package task_manager.ui.cli.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.log4j.Log4j2;
import task_manager.data.Task;
import task_manager.ui.cli.Context;

@Log4j2
public class DoneTaskCommand implements Command {

    public DoneTaskCommand(String query) {
        this.query = query;
    }

    @Override
    public void execute(Context context) {
        log.traceEntry();
        List<Task> tasks;

        try {
            tasks = context.getTaskUseCase().getTasks();
            List<Task> filteredTasks = new ArrayList<>();

            for (Task task : tasks) {
                if (task.getName().toLowerCase().contains(this.query.toLowerCase())) {
                    filteredTasks.add(task);
                }
            }

            if (filteredTasks.size() == 0) {
                System.out.println("No task matches the string '" + query + "'");
                log.info("no task matches the string '{}'", query);
            } else if (filteredTasks.size() > 1) {
                System.out.println("Multiple tasks match the string '" + query + "'");
                log.info("multiple tasks match the string '{}'", query);
            } else {
                Task task = filteredTasks.get(0);
                task.setDone(true);
                task = context.getTaskUseCase().modifyTask(task);
                log.info("marked task as done: {}", task);
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

    public final String query;

}

package task_manager.api.command;

import task_manager.db.task.JsonTaskRepository;
import task_manager.db.task.Task;
import task_manager.db.task.TaskRepository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DoneTaskCommand implements Command {

    public DoneTaskCommand(String query) {
        this.taskRepository = new JsonTaskRepository(
                new File(System.getProperty("user.home") + "/.config/task_manager/"));
        this.query = query;
    }

    @Override
    public void execute() {
        log.info("execute:");
        List<Task> tasks = null;

        try {
            tasks = taskRepository.getTasks();
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
                task = taskRepository.modifyTask(task);
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

    TaskRepository taskRepository;

    public String query;
}

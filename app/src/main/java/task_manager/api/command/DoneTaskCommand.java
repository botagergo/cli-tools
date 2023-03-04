package task_manager.api.command;

import task_manager.db.TaskRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import task_manager.db.JsonTaskRepository;
import task_manager.db.Task;

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
        } catch (IOException e) {
            System.out.println("Error getting the list of matching tasks");
            log.error("error getting the list of matching tasks:\n{}",
                    ExceptionUtils.getStackTrace(e));
            return;
        }

        List<Task> filteredTasks = tasks.stream().filter(task -> {
            return ((String) task.getName()).toLowerCase().contains(this.query.toLowerCase());
        }).collect(Collectors.toList());

        if (filteredTasks.size() == 0) {
            System.out.println("No task matches the string '" + query + "'");
            log.info("no task matches the string '{}'", query);
        } else if (filteredTasks.size() > 1) {
            System.out.println("Multiple tasks match the string '" + query + "'");
            log.info("multiple tasks match the string '{}'", query);
        } else {
            Task task = filteredTasks.get(0);
            task.setDone(true);
            try {
                task = taskRepository.modifyTask(task);
            } catch (IOException e) {
                System.out.println("Error marking task as done");
                log.error("error marking task as done: {}\n{}", task,
                        ExceptionUtils.getStackTrace(e));
            }

            log.info("marked task as done: {}", task);
        }
    }

    TaskRepository taskRepository;
    public String query;
}

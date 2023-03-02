package task_manager.api.command;

import task_manager.db.TaskRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import task_manager.db.JsonTaskRepository;

@Log4j2
public class ListTasksCommand implements Command {

    public ListTasksCommand() {
        this.taskRepository = new JsonTaskRepository(
                new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

    @Override
    public void execute() {
        log.info("execute");
        List<Map<String, Object>> tasks = null;

        try {
            tasks = taskRepository.getTasks();
        } catch (IOException e) {
            System.out.println("Error getting the list of tasks");
            log.error("error getting the list of tasks:\n{}", ExceptionUtils.getStackTrace(e));
            return;
        }

        for (Map<String, Object> task : tasks) {
            if (!task.containsKey("done") || task.get("done").equals(false)) {
                System.out.println(task.get("name"));
            }
        }
    }

    TaskRepository taskRepository;
}

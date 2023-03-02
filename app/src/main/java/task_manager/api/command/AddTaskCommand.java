package task_manager.api.command;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import task_manager.db.JsonTaskRepository;
import task_manager.db.TaskRepository;

@Log4j2
public class AddTaskCommand implements Command {

    public AddTaskCommand(Map<String, Object> task) {
        this.taskRepository = new JsonTaskRepository(
                new File(System.getProperty("user.home") + "/.config/task_manager/"));
        this.task = task;
    }

    @Override
    public void execute() {
        log.info("execute");

        if (((String) task.get("name")).isEmpty()) {
            return;
        }

        task.put("done", false);

        try {
            task = taskRepository.addTask(task);
            log.info("added task: {}", task);
        } catch (IOException e) {
            System.out.println("Error adding task");
            log.error("error adding task: {}\n{}", task, ExceptionUtils.getStackTrace(e));
        }
    }

    TaskRepository taskRepository;
    public Map<String, Object> task;

}

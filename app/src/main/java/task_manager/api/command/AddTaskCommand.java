package task_manager.api.command;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import task_manager.db.JsonTaskRepository;
import task_manager.db.TaskRepository;

public class AddTaskCommand implements Command {

    public AddTaskCommand(Map<String, Object> task) {
        this.taskRepository = new JsonTaskRepository(
                new File(System.getProperty("user.home") + "/.config/task_manager/"));
        this.task = task;
    }

    @Override
    public void execute() {
        if (((String) task.get("name")).isEmpty()) {
            return;
        }

        task.put("done", false);

        try {
            taskRepository.addTask(task);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    TaskRepository taskRepository;
    public Map<String, Object> task;

}

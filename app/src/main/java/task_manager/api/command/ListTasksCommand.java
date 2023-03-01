package task_manager.api.command;

import task_manager.db.TaskRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import task_manager.db.JsonTaskRepository;

public class ListTasksCommand implements Command {

    public ListTasksCommand() {
        this.taskRepository = new JsonTaskRepository(
                new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

    @Override
    public void execute() {
        try {
            List<Map<String, Object>> tasks = taskRepository.getTasks();
            for (Map<String, Object> task : tasks) {
                if (!task.containsKey("done") || task.get("done").equals(false)) {
                    System.out.println(task.get("name"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    TaskRepository taskRepository;

}

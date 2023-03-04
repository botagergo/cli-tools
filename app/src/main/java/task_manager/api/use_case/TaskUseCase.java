package task_manager.api.use_case;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import task_manager.db.TaskRepository;
import task_manager.db.JsonTaskRepository;
import task_manager.db.Task;

public class TaskUseCase {

    public TaskUseCase() {
        this.taskRepository = new JsonTaskRepository(
                new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

    public void addTask(Task task) throws IOException {
        task = taskRepository.addTask(task);
    }

    public List<Task> getTasks() throws IOException {
        return taskRepository.getTasks();
    }

    private TaskRepository taskRepository;

}

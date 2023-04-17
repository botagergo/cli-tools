package task_manager.api.use_case;

import java.io.IOException;
import java.util.List;
import com.google.inject.Inject;
import task_manager.db.task.Task;
import task_manager.db.task.TaskRepository;

public class TaskUseCase {

    public Task addTask(Task task) throws IOException {
        return taskRepository.addTask(task);
    }

    public Task modifyTask(Task task) throws IOException {
        return taskRepository.modifyTask(task);
    }

    public List<Task> getTasks() throws IOException {
        return taskRepository.getTasks();
    }

    public void deleteAllTasks() throws IOException {
        taskRepository.deleteAllTasks();
    }

    @Inject
    private TaskRepository taskRepository;

}

package task_manager.logic.use_case;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import jakarta.inject.Inject;
import task_manager.repository.TaskRepository;
import task_manager.data.Task;

public class TaskUseCase {

    @Inject
    public TaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task addTask(Task task) throws IOException {
        task.asMap().put("uuid", UUID.randomUUID().toString());
        return taskRepository.create(task);
    }

    public Task modifyTask(Task task) throws IOException {
        return taskRepository.update(task);
    }

    public boolean deleteTask(UUID uuid) throws IOException {
        return taskRepository.delete(uuid);
    }

    public List<Task> getTasks() throws IOException {
        return taskRepository.getAll();
    }

    public Task getTask(UUID uuid) throws IOException {
        return taskRepository.get(uuid);
    }

    public void deleteAllTasks() throws IOException {
        taskRepository.deleteAll();
    }

    private final TaskRepository taskRepository;

}

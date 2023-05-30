package task_manager.logic.use_case;

import org.apache.commons.lang3.NotImplementedException;
import task_manager.data.Task;
import task_manager.property.PropertyException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

public class TaskUseCaseClientImpl implements TaskUseCase {

    public TaskUseCaseClientImpl(TaskManagerClient taskManagerClient) {
        this.taskManagerClient = taskManagerClient;
    }

    @Override
    public Task addTask(Task task) throws IOException {
        try {
            return taskManagerClient.postJson("/api/v1/tasks", task, Task.class);
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Task modifyTask(Task task) {
        throw new NotImplementedException();
    }

    @Override
    public boolean deleteTask(UUID uuid) {
        throw new NotImplementedException();
    }

    @Override
    public List<Task> getTasks() {
        throw new NotImplementedException();
    }

    @Override
    public List<Task> getTasks(String query) {
        throw new NotImplementedException();
    }

    @Override
    public Task getTask(UUID uuid) {
        throw new NotImplementedException();
    }

    @Override
    public void deleteAllTasks() {
        throw new NotImplementedException();
    }

    private TaskManagerClient taskManagerClient;

}

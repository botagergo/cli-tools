package task_manager.logic.use_case.task;

import org.apache.commons.lang3.NotImplementedException;
import task_manager.core.data.FilterCriterionInfo;
import task_manager.core.data.SortingInfo;
import task_manager.core.data.Task;
import task_manager.core.property.PropertySpec;
import task_manager.logic.use_case.TaskManagerClient;

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
    public List<Task> getTasks(List<String> queries, List<PropertySpec> propertySpecs, SortingInfo sortingInfo, FilterCriterionInfo filterCriterionInfo) {
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

    private final TaskManagerClient taskManagerClient;

}

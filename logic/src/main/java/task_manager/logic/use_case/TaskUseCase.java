package task_manager.logic.use_case;

import task_manager.data.Task;
import task_manager.filter.FilterCriterion;
import task_manager.property.PropertyException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TaskUseCase {
    Task addTask(Task task) throws IOException;

    Task modifyTask(Task task) throws IOException;

    boolean deleteTask(UUID uuid) throws IOException;

    List<Task> getTasks() throws IOException;

    List<Task> getTasks(String nameQuery, List<String> queries, List<FilterCriterion> filterCriteria) throws IOException, PropertyException;

    Task getTask(UUID uuid) throws IOException;

    void deleteAllTasks() throws IOException;
}

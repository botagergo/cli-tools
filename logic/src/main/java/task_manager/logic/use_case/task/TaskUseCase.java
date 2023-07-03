package task_manager.logic.use_case.task;

import task_manager.core.data.FilterCriterionInfo;
import task_manager.core.data.SortingInfo;
import task_manager.core.data.Task;
import task_manager.core.property.FilterPropertySpec;
import task_manager.core.property.PropertyException;
import task_manager.logic.filter.FilterCriterionException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TaskUseCase {
    Task addTask(Task task) throws IOException;

    Task modifyTask(Task task) throws IOException;

    boolean deleteTask(UUID uuid) throws IOException;

    List<Task> getTasks() throws IOException;

    List<Task> getTasks(
            List<String> queries,
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo
    ) throws IOException, TaskUseCaseException, PropertyException, PropertyConverterException, FilterCriterionException;

    Task getTask(UUID uuid) throws IOException;

    void deleteAllTasks() throws IOException;
}

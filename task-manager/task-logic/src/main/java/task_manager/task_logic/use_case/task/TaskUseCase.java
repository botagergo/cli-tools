package task_manager.task_logic.use_case.task;

import task_manager.core.data.FilterCriterionInfo;
import task_manager.core.data.SortingInfo;
import task_manager.core.data.Task;
import task_manager.core.property.FilterPropertySpec;
import task_manager.logic.filter.FilterCriterionException;
import task_manager.property_lib.PropertyException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TaskUseCase {
    Task addTask(Task task) throws IOException;

    Task modifyTask(Task task) throws IOException, TaskUseCaseException;

    void deleteTask(UUID uuid) throws IOException, TaskUseCaseException;

    List<Task> getTasks() throws IOException;

    List<Task> getTasks(
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs) throws IOException, TaskUseCaseException, PropertyException, PropertyConverterException, FilterCriterionException;

    void deleteAllTasks() throws IOException;
}

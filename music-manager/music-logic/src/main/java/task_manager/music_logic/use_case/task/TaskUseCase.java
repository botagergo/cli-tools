package common.music_logic.use_case.task;

import common.core.data.FilterCriterionInfo;
import common.core.data.SortingInfo;
import common.core.data.Task;
import common.core.property.FilterPropertySpec;
import common.service.filter.FilterCriterionException;
import common.property_lib.PropertyException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TaskUseCase {
    Task addTask(Task task) throws IOException;

    Task modifyTask(Task task) throws IOException, TaskUseCaseException;

    void deleteTask(UUID uuid) throws IOException, TaskUseCaseException;

    List<Task> getTasks() throws IOException;

    List<Task> getTasks(
            List<String> queries,
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs) throws IOException, TaskUseCaseException, PropertyException, PropertyConverterException, FilterCriterionException;

    Task getTask(UUID uuid) throws IOException;

    void deleteAllTasks() throws IOException;
}

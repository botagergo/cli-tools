package cli_tools.task_manager.task.service;

import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.SortingInfo;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.filter.FilterCriterionException;
import cli_tools.common.property_converter.PropertyConverterException;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.task_manager.task.PropertyOwnerTree;
import cli_tools.task_manager.task.Task;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    Task addTask(@NonNull Task task) throws IOException;

    Task modifyTask(@NonNull UUID taskUuid, @NonNull Task task) throws IOException, TaskServiceException;

    Task doneTask(@NonNull UUID taskUuid) throws IOException, TaskServiceException;

    Task undoneTask(@NonNull UUID taskUuid) throws IOException, TaskServiceException;

    void deleteTask(@NonNull UUID uuid) throws IOException, TaskServiceException;

    List<Task> getTasks(boolean getDone) throws IOException;

    List<Task> getTasks(
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs, boolean getDone) throws IOException, TaskServiceException, PropertyException, PropertyConverterException, FilterCriterionException;

    List<PropertyOwnerTree> getTaskTrees(
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs, boolean getDone) throws IOException, TaskServiceException, PropertyException, PropertyConverterException, FilterCriterionException;

    void deleteAllTasks() throws IOException;
}

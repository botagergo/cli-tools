package cli_tools.task_manager.backend.task.service;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.SortingInfo;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.task_manager.backend.task.PropertyOwnerTree;
import cli_tools.task_manager.backend.task.Task;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    Task addTask(@NonNull Task task) throws ServiceException;

    Task modifyTask(@NonNull Task task) throws ServiceException;

    Task doneTask(@NonNull UUID taskUuid) throws ServiceException;

    Task undoneTask(@NonNull UUID taskUuid) throws ServiceException;

    void deleteTask(@NonNull UUID uuid) throws ServiceException;

    List<Task> getTasks(boolean getDone, List<FilterPropertySpec> filterPropertySpecs) throws ServiceException;

    List<Task> getTasks(
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs, boolean getDone) throws ServiceException;

    List<PropertyOwnerTree> getTaskTrees(
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs, boolean getDone) throws ServiceException;

    void deleteAllTasks() throws IOException;
}

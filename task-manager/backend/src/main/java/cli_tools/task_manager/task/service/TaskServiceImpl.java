package cli_tools.task_manager.task.service;

import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.SortingInfo;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.filter.AndFilterCriterion;
import cli_tools.common.filter.Filter;
import cli_tools.common.filter.FilterCriterion;
import cli_tools.common.filter.SimpleFilter;
import cli_tools.common.property_comparator.PropertyNotComparableException;
import cli_tools.common.property_converter.PropertyConverter;
import cli_tools.common.property_converter.PropertyConverterException;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.sorter.PropertySorter;
import cli_tools.common.temp_id_mapping.TempIDManager;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.task_manager.task.PropertyOwnerTree;
import cli_tools.task_manager.task.Task;
import cli_tools.task_manager.task.repository.TaskRepository;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@AllArgsConstructor(onConstructor = @__(@Inject))
@Getter
@Setter
@Log4j2
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private TaskRepository doneTaskRepository;
    private PropertyManager propertyManager;
    private UUIDGenerator uuidGenerator;
    private PropertyConverter propertyConverter;
    private TempIDManager tempIdManager;

    @Override
    public @NonNull Task addTask(@NonNull Task task) throws IOException {
        task.getProperties().put("uuid", uuidGenerator.getUUID());
        for (PropertyDescriptor propertyDescriptor : propertyManager.getPropertyDescriptorCollection().getAll().values()) {
            if (propertyDescriptor.defaultValue() != null && !task.getProperties().containsKey(propertyDescriptor.name())) {
                task.getProperties().put(propertyDescriptor.name(), propertyDescriptor.defaultValue());
            }
        }
        return taskRepository.create(task);
    }

    @Override
    public @NonNull Task modifyTask(@NonNull UUID taskUuid, @NonNull Task task) throws IOException, TaskServiceException {
        Task modifiedTask = taskRepository.update(taskUuid, task);
        if (modifiedTask == null) {
            throw new TaskServiceException(String.format(TaskServiceException.taskNotFoundMessage, taskUuid));
        }
        return modifiedTask;
    }

    @Override
    public Task doneTask(@NonNull UUID taskUuid) throws IOException, TaskServiceException {
        Task task = taskRepository.delete(taskUuid);
        if (task == null) {
            throw new TaskServiceException(String.format(TaskServiceException.taskNotFoundMessage, taskUuid));
        }
        return doneTaskRepository.create(task);
    }

    @Override
    public Task undoneTask(@NonNull UUID taskUuid) throws IOException, TaskServiceException {
        Task task = doneTaskRepository.delete(taskUuid);
        if (task == null) {
            throw new TaskServiceException(String.format(TaskServiceException.taskNotFoundMessage, taskUuid));
        }
        return taskRepository.create(task);
    }

    @Override
    public void deleteTask(@NonNull UUID uuid) throws IOException, TaskServiceException {
        if (taskRepository.delete(uuid) == null) {
            throw new TaskServiceException(String.format(TaskServiceException.taskNotFoundMessage, uuid));
        }
        tempIdManager.delete(uuid);
    }

    @Override
    public List<Task> getTasks(boolean getDone) throws IOException {
        List<Task> tasks = taskRepository.getAll();
        tasks.forEach(task -> task.setDone(false));
        if (getDone) {
            List<Task> doneTasks = doneTaskRepository.getAll();
            doneTasks.forEach(task -> task.setDone(true));
            return Stream.concat(tasks.stream(), doneTasks.stream()).toList();
        } else {
            return tasks;
        }
    }

    @Override
    public List<Task> getTasks(
            List<FilterPropertySpec> filterPropertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs,
            boolean getDone) throws IOException, TaskServiceException, PropertyException, PropertyConverterException {

        List<Task> tasks = getUnsortedTasks(filterPropertySpecs, filterCriterionInfo, taskUUIDs, getDone);

        if (sortingInfo != null) {
            PropertySorter<Task> propertySorter = new PropertySorter<>(sortingInfo.sortingCriteria());
            try {
                tasks = propertySorter.sort(tasks, propertyManager);
            } catch (PropertyException | PropertyNotComparableException e) {
                throw new TaskServiceException("failed to sort tasks: " + e.getMessage());
            }
        }

        return tasks;
    }

    @Override
    public List<PropertyOwnerTree> getTaskTrees(
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs,
            boolean getDone) throws IOException, TaskServiceException, PropertyException, PropertyConverterException {
        List<Task> tasks = getUnsortedTasks(propertySpecs, filterCriterionInfo, taskUUIDs, getDone);

        List<PropertyOwnerTree> taskTrees = new ArrayList<>();
        Map<UUID, PropertyOwnerTree> taskTreeMap = new HashMap<>();

        for (Task task : tasks) {
            getTaskTree(taskTrees, taskTreeMap, task);
        }

        if (sortingInfo != null) {
            PropertySorter<PropertyOwnerTree> sorter = new PropertySorter<>(sortingInfo.sortingCriteria());
            try {
                taskTrees = sortTaskTrees(taskTrees, sorter, propertyManager);
            } catch (PropertyException | PropertyNotComparableException e) {
                throw new TaskServiceException("failed to sort tasks: " + e.getMessage());
            }
        }

        return taskTrees;
    }

    @Override
    public void deleteAllTasks() throws IOException {
        taskRepository.deleteAll();
        tempIdManager.deleteAll();
    }

    private PropertyOwnerTree getTaskTree(List<PropertyOwnerTree> taskTrees, Map<UUID, PropertyOwnerTree> taskTreeMap, Task task) throws IOException, PropertyException, TaskServiceException {
        PropertyOwnerTree taskTree = taskTreeMap.get(task.getUUID());
        if (taskTree != null) {
            return taskTree;
        }

        taskTree = new PropertyOwnerTree(task, new ArrayList<>());
        taskTreeMap.put(task.getUUID(), taskTree);

        UUID parentUuid = propertyManager.getProperty(taskTree.getParent(), "parent").getUuid();

        if (parentUuid != null) {
            PropertyOwnerTree parentTaskTree = taskTreeMap.get(parentUuid);
            if (parentTaskTree == null) {
                parentTaskTree = getTaskTree(taskTrees, taskTreeMap, parentUuid);
            }
            if (parentTaskTree.getChildren() == null) {
                parentTaskTree.setChildren(new ArrayList<>());
            }
            parentTaskTree.getChildren().add(taskTree);
        } else {
            taskTrees.add(taskTree);
        }

        return taskTree;
    }

    private PropertyOwnerTree getTaskTree(List<PropertyOwnerTree> taskTrees, Map<UUID, PropertyOwnerTree> taskTreeMap, UUID uuid) throws IOException, PropertyException, TaskServiceException {
        PropertyOwnerTree taskTree = taskTreeMap.get(uuid);
        if (taskTree != null) {
            return taskTree;
        }

        Task parent = taskRepository.get(uuid);
        if (parent == null) {
            throw new TaskServiceException(String.format(TaskServiceException.taskNotFoundMessage, uuid));
        }

        taskTree = new PropertyOwnerTree(taskRepository.get(uuid), new ArrayList<>());
        taskTreeMap.put(uuid, taskTree);

        UUID parentUuid = propertyManager.getProperty(taskTree.getParent(), "parent").getUuid();
        if (parentUuid != null) {
            PropertyOwnerTree parentTaskTree = taskTreeMap.get(parentUuid);
            if (parentTaskTree == null) {
                parentTaskTree = getTaskTree(taskTrees, taskTreeMap, taskRepository.get(parentUuid));
            }
            if (parentTaskTree.getChildren() == null) {
                parentTaskTree.setChildren(new ArrayList<>());
            }
            parentTaskTree.getChildren().add(taskTree);
        } else {
            taskTrees.add(taskTree);
        }

        return taskTree;
    }

    private List<Task> getUnsortedTasks(
            List<FilterPropertySpec> filterPropertySpecs,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs,
            boolean getDone
    ) throws IOException, TaskServiceException, PropertyException, PropertyConverterException {
        List<FilterCriterion> finalFilterCriteria = new ArrayList<>();

        List<Task> tasks;
        if (taskUUIDs != null) {
            Set<UUID> uuids = new HashSet<>();
            tasks = new ArrayList<>();
            for (UUID taskUUID : taskUUIDs) {
                if (uuids.contains(taskUUID)) {
                    continue;
                }
                uuids.add(taskUUID);
                Task task = taskRepository.get(taskUUID);

                if (task == null && getDone) {
                    task = doneTaskRepository.get(taskUUID);
                }

                if (task != null) {
                    tasks.add(task);
                }
            }
        } else {
            tasks = getTasks(getDone);
        }

        if (filterCriterionInfo != null) {
            finalFilterCriteria.add(FilterCriterion.from(filterCriterionInfo, propertyManager, propertyConverter));
        }

        if (filterPropertySpecs != null) {
            for (FilterPropertySpec filterPropertySpec : filterPropertySpecs) {
                FilterCriterion filterCriterion = FilterCriterion.from(filterPropertySpec);
                finalFilterCriteria.add(filterCriterion);
            }
        }

        if (!finalFilterCriteria.isEmpty()) {
            Filter filter = new SimpleFilter(new AndFilterCriterion(finalFilterCriteria));
            try {
                tasks = filter.doFilter(tasks, propertyManager);
            } catch (PropertyException e) {
                throw new TaskServiceException("Failed to filter tasks: " + e.getMessage());
            }
        }

        return tasks;
    }

    private List<PropertyOwnerTree> sortTaskTrees(List<PropertyOwnerTree> taskTrees, PropertySorter<PropertyOwnerTree> propertySorter, PropertyManager propertyManager) throws PropertyException, PropertyNotComparableException, IOException {
        taskTrees = propertySorter.sort(taskTrees, propertyManager);
        for (PropertyOwnerTree taskTree : taskTrees) {
            if (taskTree.getChildren() != null) {
                taskTree.setChildren(sortTaskTrees(taskTree.getChildren(), propertySorter, propertyManager));
            }
        }
        return taskTrees;
    }

}

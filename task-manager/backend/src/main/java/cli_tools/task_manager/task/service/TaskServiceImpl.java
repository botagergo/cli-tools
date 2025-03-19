package cli_tools.task_manager.task.service;

import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.filter.*;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;
import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.SortingInfo;
import cli_tools.task_manager.task.Task;
import cli_tools.task_manager.task.TaskHierarchy;
import cli_tools.task_manager.task.repository.TaskRepository;
import cli_tools.common.property_comparator.PropertyComparator;
import cli_tools.common.property_comparator.PropertyNotComparableException;
import cli_tools.common.property_converter.PropertyConverter;
import cli_tools.common.property_converter.PropertyConverterException;
import cli_tools.common.sorter.PropertySorter;
import cli_tools.common.temp_id_mapping.service.TempIDMappingService;
import cli_tools.common.util.UUIDGenerator;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor(onConstructor = @__(@Inject))
@Getter
@Setter
public class TaskServiceImpl implements TaskService {

    @Override
    public @NonNull Task addTask(@NonNull Task task) throws IOException {
        task.getProperties().put("uuid", uuidGenerator.getUUID());
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
    public void deleteTask(@NonNull UUID uuid) throws IOException, TaskServiceException {
        if (!taskRepository.delete(uuid)) {
            throw new TaskServiceException(String.format(TaskServiceException.taskNotFoundMessage, uuid));
        }
        tempIDMappingService.delete(uuid);
    }

    @Override
    public List<Task> getTasks() throws IOException {
        return taskRepository.getAll();
    }

    @Override
    public List<Task> getTasks(
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs
    ) throws IOException, TaskServiceException, PropertyException, PropertyConverterException, FilterCriterionException {

        List<Task> tasks = getUnsortedTasks(propertySpecs, filterCriterionInfo, taskUUIDs);

        if (sortingInfo != null) {
            PropertySorter<Task> propertySorter = new PropertySorter<>(sortingInfo.sortingCriteria());
            try {
                propertySorter.sort(tasks, propertyManager);
            } catch (PropertyException | PropertyNotComparableException e) {
                throw new TaskServiceException("Failed to sort tasks: " + e.getMessage());
            }
        }

        return tasks;
    }


    public List<TaskHierarchy> getTaskHierarchies(
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs
    ) throws IOException, TaskServiceException, PropertyException, PropertyConverterException, FilterCriterionException {
        List<Task> tasks = getUnsortedTasks(propertySpecs, filterCriterionInfo, taskUUIDs);

        List<TaskHierarchy> taskHierarchies = new ArrayList<>();
        Map<UUID, TaskHierarchy> taskTreeMap = new HashMap<>();

        for (Task task : tasks) {
            addTaskHierarchy(taskHierarchies, taskTreeMap, task);
        }

        if (sortingInfo != null) {
            PropertySorter<TaskHierarchy> sorter = new PropertySorter<>(sortingInfo.sortingCriteria());
            try {
                sortTaskTrees(taskHierarchies, sorter, propertyManager);
            } catch (PropertyException | PropertyNotComparableException e) {
                throw new TaskServiceException("Failed to sort tasks: " + e.getMessage());
            }
        }

        return taskHierarchies;
    }

    private TaskHierarchy addTaskHierarchy(List<TaskHierarchy> taskHierarchies, Map<UUID, TaskHierarchy> taskTreeMap, Task task) throws IOException, PropertyException {
        TaskHierarchy taskHierarchy = taskTreeMap.get(task.getUUID());
        if (taskHierarchy != null) {
            return taskHierarchy;
        }

        taskHierarchy = new TaskHierarchy(task, new ArrayList<>());
        taskTreeMap.put(task.getUUID(), taskHierarchy);

        UUID parentUuid = propertyManager.getProperty(taskHierarchy.getParent(), "parent").getUuid();
        if (parentUuid != null) {
            TaskHierarchy parentTaskHierarchy = taskTreeMap.get(parentUuid);
            if (parentTaskHierarchy == null) {
                parentTaskHierarchy = addTaskHierarchy(taskHierarchies, taskTreeMap, parentUuid);
            }
            if (parentTaskHierarchy.getChildren() == null) {
                parentTaskHierarchy.setChildren(new ArrayList<>());
            }
            parentTaskHierarchy.getChildren().add(taskHierarchy);
        } else {
            taskHierarchies.add(taskHierarchy);
        }

        return taskHierarchy;
    }

    private TaskHierarchy addTaskHierarchy(List<TaskHierarchy> taskHierarchies, Map<UUID, TaskHierarchy> taskTreeMap, UUID uuid) throws IOException, PropertyException {
        TaskHierarchy taskHierarchy = taskTreeMap.get(uuid);
        if (taskHierarchy != null) {
            return taskHierarchy;
        }

        taskHierarchy = new TaskHierarchy(getTask(uuid), new ArrayList<>());
        taskTreeMap.put(uuid, taskHierarchy);

        UUID parentUuid = propertyManager.getProperty(taskHierarchy.getParent(), "parent").getUuid();
        if (parentUuid != null) {
            TaskHierarchy parentTaskHierarchy = taskTreeMap.get(parentUuid);
            if (parentTaskHierarchy == null) {
                parentTaskHierarchy = addTaskHierarchy(taskHierarchies, taskTreeMap, getTask(parentUuid));
            }
            if (parentTaskHierarchy.getChildren() == null) {
                parentTaskHierarchy.setChildren(new ArrayList<>());
            }
            parentTaskHierarchy.getChildren().add(taskHierarchy);
        } else {
            taskHierarchies.add(taskHierarchy);
        }

        return taskHierarchy;
    }

    private List<Task> getUnsortedTasks(
            List<FilterPropertySpec> propertySpecs,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs
    ) throws IOException, TaskServiceException, PropertyException, PropertyConverterException, FilterCriterionException {
        List<FilterCriterion> finalFilterCriteria = new ArrayList<>();

        List<Task> tasks;
        if (taskUUIDs != null) {
            Set<UUID> uuids = new HashSet<>();
            tasks = new ArrayList<>();
            for(UUID taskUUID : taskUUIDs) {
                if (uuids.contains(taskUUID)) {
                    continue;
                }
                uuids.add(taskUUID);
                Task task = getTask(taskUUID);
                if (task != null) {
                    tasks.add(getTask(taskUUID));
                }
            }
        } else {
            tasks = getTasks();
        }

        if (filterCriterionInfo != null) {
            finalFilterCriteria.add(createFilterCriterion(filterCriterionInfo, propertyManager));
        }

        if (propertySpecs != null) {
            for (FilterPropertySpec propertySpec : propertySpecs) {
                FilterCriterion filterCriterion = getFilterCriterion(propertySpec);

                if (propertySpec.negate()) {
                    filterCriterion = new NotFilterCriterion(filterCriterion);
                }

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


    private void sortTaskTrees(List<TaskHierarchy> taskHierarchies, PropertySorter<TaskHierarchy> propertySorter, PropertyManager propertyManager) throws PropertyException, PropertyNotComparableException, IOException {
        propertySorter.sort(taskHierarchies, propertyManager);
        for (TaskHierarchy taskHierarchy : taskHierarchies) {
            if (taskHierarchy.getChildren() != null) {
                sortTaskTrees(taskHierarchy.getChildren(), propertySorter, propertyManager);
            }
        }
    }

    private Task getTask(UUID uuid) throws IOException {
        return taskRepository.get(uuid);
    }

    @Override
    public void deleteAllTasks() throws IOException {
        taskRepository.deleteAll();
        tempIDMappingService.deleteAll();
    }

    private FilterCriterion getFilterCriterion(FilterPropertySpec propertySpec) throws TaskServiceException {
        if (propertySpec.predicate() == null) {
            return new EqualFilterCriterion(
                    propertySpec.property().getPropertyDescriptor().name(),
                    propertySpec.property().getValue());
        }

        switch (propertySpec.predicate()) {
            case EQUALS -> {
                return new EqualFilterCriterion(
                        propertySpec.property().getPropertyDescriptor().name(),
                        propertySpec.property().getValue());
            }
            case CONTAINS -> {
                try {
                    if (propertySpec.property().getPropertyDescriptor().type() == PropertyDescriptor.Type.String) {
                        return new ContainsCaseInsensitiveFilterCriterion(
                                propertySpec.property().getPropertyDescriptor().name(),
                                propertySpec.property().getString());
                    } else if (propertySpec.property().getPropertyDescriptor().isCollection()) {
                        return new CollectionContainsFilterCriterion(
                                propertySpec.property().getPropertyDescriptor().name(),
                                propertySpec.property().getCollection());
                    } else {
                        throw new TaskServiceException("Illegal type for CONTAINS predicate: "
                                + propertySpec.property().getPropertyDescriptor());
                    }
                } catch (PropertyException e) {
                    throw new TaskServiceException("Failed to filter tasks: " + e.getMessage());
                }
            }
            case LESS -> {
                if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                    throw new TaskServiceException("Illegal type for LESS predicate: "
                            + propertySpec.property().getPropertyDescriptor());
                }
                return new LessFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                        propertySpec.property(), new PropertyComparator(true));
            }
            case LESS_EQUAL -> {
                if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                    throw new TaskServiceException("Illegal type for LESS_EQUAL predicate: "
                            + propertySpec.property().getPropertyDescriptor());
                }
                return new LessEqualFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                        propertySpec.property(), new PropertyComparator(true));
            }
            case GREATER -> {
                if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                    throw new TaskServiceException("Illegal type for GREATER predicate: "
                            + propertySpec.property().getPropertyDescriptor());
                }
                return new GreaterFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                        propertySpec.property(), new PropertyComparator(false));
            }
            case GREATER_EQUAL -> {
                if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                    throw new TaskServiceException("Illegal type for GREATER_EQUAL predicate: "
                            + propertySpec.property().getPropertyDescriptor());
                }
                return new GreaterEqualFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                        propertySpec.property(), new PropertyComparator(false));
            }
        }

        throw new RuntimeException();
    }

    private FilterCriterion createFilterCriterion(@NonNull FilterCriterionInfo filterCriterionInfo, @NonNull PropertyManager propertyManager) throws PropertyException, IOException, PropertyConverterException, FilterCriterionException {
        switch (filterCriterionInfo.type()) {
            case PROPERTY -> {
                return createPropertyFilterCriterion(filterCriterionInfo, propertyManager);
            }
            case AND -> {
                ArrayList<FilterCriterion> filterCriteria = new ArrayList<>();
                for (FilterCriterionInfo filterCriterionInfo_ : filterCriterionInfo.children()) {
                    filterCriteria.add(createFilterCriterion(filterCriterionInfo_, propertyManager));
                }
                return new AndFilterCriterion(filterCriteria);
            }
            case OR -> {
                ArrayList<FilterCriterion> filterCriteria = new ArrayList<>();
                for (FilterCriterionInfo filterCriterionInfo_ : filterCriterionInfo.children()) {
                    filterCriteria.add(createFilterCriterion(filterCriterionInfo_, propertyManager));
                }
                return new OrFilterCriterion(filterCriteria);
            }
            case NOT -> {
                return new NotFilterCriterion(createFilterCriterion(
                        filterCriterionInfo.children().get(0), propertyManager));
            }
            default -> throw new NotImplementedException(filterCriterionInfo.type() + " filter criterion is not yet supported");
        }
    }

    @SuppressWarnings("unchecked")
    private FilterCriterion createPropertyFilterCriterion(FilterCriterionInfo filterCriterionInfo, PropertyManager propertyManager) throws PropertyException, IOException, PropertyConverterException, FilterCriterionException {
        PropertyDescriptor propertyDescriptor = propertyManager.getPropertyDescriptor(filterCriterionInfo.propertyName());
        Object operand = propertyConverter.convertProperty(propertyDescriptor, filterCriterionInfo.operands());

        switch (filterCriterionInfo.predicate()) {
            case EQUALS -> {
                return new EqualFilterCriterion(filterCriterionInfo.propertyName(), operand);
            }
            case CONTAINS -> {
                if (propertyDescriptor.isCollection()) {
                    return new CollectionContainsFilterCriterion(filterCriterionInfo.propertyName(), (Collection<Object>) operand);
                } else if (propertyDescriptor.type() == PropertyDescriptor.Type.String) {
                    return new ContainsCaseInsensitiveFilterCriterion(filterCriterionInfo.propertyName(), (String) operand);
                } else {
                    throw new FilterCriterionException(FilterCriterionException.Type.InvalidTypeForPredicate, propertyDescriptor, filterCriterionInfo.predicate());
                }
            }
            case LESS -> {
                if (!PropertyComparator.isComparable(propertyDescriptor)) {
                    throw new FilterCriterionException(FilterCriterionException.Type.InvalidTypeForPredicate, propertyDescriptor, filterCriterionInfo.predicate());
                } else {
                    return new LessFilterCriterion(filterCriterionInfo.propertyName(), Property.fromUnchecked(propertyDescriptor, operand), new PropertyComparator(true));
                }
            }
            case LESS_EQUAL -> {
                if (!PropertyComparator.isComparable(propertyDescriptor)) {
                    throw new FilterCriterionException(FilterCriterionException.Type.InvalidTypeForPredicate, propertyDescriptor, filterCriterionInfo.predicate());
                } else {
                    return new LessEqualFilterCriterion(filterCriterionInfo.propertyName(), Property.fromUnchecked(propertyDescriptor, operand), new PropertyComparator(true));
                }
            }
            case GREATER -> {
                if (!PropertyComparator.isComparable(propertyDescriptor)) {
                    throw new FilterCriterionException(FilterCriterionException.Type.InvalidTypeForPredicate, propertyDescriptor, filterCriterionInfo.predicate());
                } else {
                    return new GreaterFilterCriterion(filterCriterionInfo.propertyName(), Property.fromUnchecked(propertyDescriptor, operand), new PropertyComparator(true));
                }
            }
            case GREATER_EQUAL -> {
                if (!PropertyComparator.isComparable(propertyDescriptor)) {
                    throw new FilterCriterionException(FilterCriterionException.Type.InvalidTypeForPredicate, propertyDescriptor, filterCriterionInfo.predicate());
                } else {
                    return new GreaterEqualFilterCriterion(filterCriterionInfo.propertyName(), Property.fromUnchecked(propertyDescriptor, operand), new PropertyComparator(true));
                }
            }
        }

        throw new RuntimeException();
    }

    private TaskRepository taskRepository;
    private PropertyManager propertyManager;
    private UUIDGenerator uuidGenerator;
    private PropertyConverter propertyConverter;
    private TempIDMappingService tempIDMappingService;

}

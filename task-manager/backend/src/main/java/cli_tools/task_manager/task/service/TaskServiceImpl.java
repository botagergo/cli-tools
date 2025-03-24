package cli_tools.task_manager.task.service;

import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.Predicate;
import cli_tools.common.core.data.SortingInfo;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.filter.*;
import cli_tools.common.property_comparator.PropertyComparator;
import cli_tools.common.property_comparator.PropertyNotComparableException;
import cli_tools.common.property_converter.PropertyConverter;
import cli_tools.common.property_converter.PropertyConverterException;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.sorter.PropertySorter;
import cli_tools.common.temp_id_mapping.service.TempIDMappingService;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.task_manager.task.Task;
import cli_tools.task_manager.task.PropertyOwnerTree;
import cli_tools.task_manager.task.repository.TaskRepository;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor(onConstructor = @__(@Inject))
@Getter
@Setter
@Log4j2
public class TaskServiceImpl implements TaskService {

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
            List<FilterPropertySpec> filterPropertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs
    ) throws IOException, TaskServiceException, PropertyException, PropertyConverterException {

        List<Task> tasks = getUnsortedTasks(filterPropertySpecs, filterCriterionInfo, taskUUIDs);

        if (sortingInfo != null) {
            PropertySorter<Task> propertySorter = new PropertySorter<>(sortingInfo.sortingCriteria());
            try {
                propertySorter.sort(tasks, propertyManager);
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
            List<UUID> taskUUIDs
    ) throws IOException, TaskServiceException, PropertyException, PropertyConverterException {
        List<Task> tasks = getUnsortedTasks(propertySpecs, filterCriterionInfo, taskUUIDs);

        List<PropertyOwnerTree> taskTrees = new ArrayList<>();
        Map<UUID, PropertyOwnerTree> taskTreeMap = new HashMap<>();

        for (Task task : tasks) {
            getTaskTree(taskTrees, taskTreeMap, task);
        }

        if (sortingInfo != null) {
            PropertySorter<PropertyOwnerTree> sorter = new PropertySorter<>(sortingInfo.sortingCriteria());
            try {
                sortTaskTrees(taskTrees, sorter, propertyManager);
            } catch (PropertyException | PropertyNotComparableException e) {
                throw new TaskServiceException("failed to sort tasks: " + e.getMessage());
            }
        }

        return taskTrees;
    }

    private PropertyOwnerTree getTaskTree(List<PropertyOwnerTree> taskTrees, Map<UUID, PropertyOwnerTree> taskTreeMap, Task task) throws IOException, PropertyException {
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

    private PropertyOwnerTree getTaskTree(List<PropertyOwnerTree> taskTrees, Map<UUID, PropertyOwnerTree> taskTreeMap, UUID uuid) throws IOException, PropertyException {
        PropertyOwnerTree taskTree = taskTreeMap.get(uuid);
        if (taskTree != null) {
            return taskTree;
        }

        taskTree = new PropertyOwnerTree(getTask(uuid), new ArrayList<>());
        taskTreeMap.put(uuid, taskTree);

        UUID parentUuid = propertyManager.getProperty(taskTree.getParent(), "parent").getUuid();
        if (parentUuid != null) {
            PropertyOwnerTree parentTaskTree = taskTreeMap.get(parentUuid);
            if (parentTaskTree == null) {
                parentTaskTree = getTaskTree(taskTrees, taskTreeMap, getTask(parentUuid));
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
            List<UUID> taskUUIDs
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

        if (filterPropertySpecs != null) {
            for (FilterPropertySpec filterPropertySpec : filterPropertySpecs) {
                FilterCriterion filterCriterion = getFilterCriterion(filterPropertySpec);

                if (filterPropertySpec.negate()) {
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


    private void sortTaskTrees(List<PropertyOwnerTree> taskTrees, PropertySorter<PropertyOwnerTree> propertySorter, PropertyManager propertyManager) throws PropertyException, PropertyNotComparableException, IOException {
        propertySorter.sort(taskTrees, propertyManager);
        for (PropertyOwnerTree taskTree : taskTrees) {
            if (taskTree.getChildren() != null) {
                sortTaskTrees(taskTree.getChildren(), propertySorter, propertyManager);
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

    private FilterCriterion getFilterCriterion(FilterPropertySpec filterPropertySpec) throws TaskServiceException {
        List<Object> operand = filterPropertySpec.operand();
        return createFilterCriterion(filterPropertySpec.propertyDescriptor(), filterPropertySpec.predicate(), operand);
    }

    private FilterCriterion createFilterCriterion(
            PropertyDescriptor propertyDescriptor, Predicate predicate, List<Object> operand
    ) throws TaskServiceException {
        String propertyName = propertyDescriptor.name();

        if (predicate == null || predicate.equals(Predicate.EQUALS)) {
            Object finalOperand;
            if (propertyDescriptor.isList()) {
                finalOperand = operand;
            } else if (propertyDescriptor.isSet()) {
                finalOperand = Set.copyOf(operand);
            } else if (operand.size() != 1) {
                throw new TaskServiceException("one argument expected for predicate 'equals' of property '" + propertyName + "'");
            } else {
                finalOperand = operand.get(0);
            }
            return new EqualFilterCriterion(propertyName, finalOperand);
        } else if (predicate == Predicate.LESS || predicate == Predicate.LESS_EQUAL ||
                predicate == Predicate.GREATER || predicate == Predicate.GREATER_EQUAL) {
            if (!PropertyComparator.isComparable(propertyDescriptor)) {
                throw new TaskServiceException("predicate: " + predicate + " is incompatible with property '" + propertyName + "'");
            } else if (operand.size() != 1) {
                throw new TaskServiceException("one argument expected for predicate '" + predicate + "' of property '" + propertyName + "'");
            }
            Property property = Property.fromUnchecked(propertyDescriptor, operand.get(0));
            switch (predicate) {
                case LESS -> {
                    return new LessFilterCriterion(propertyName, property, new PropertyComparator(true));
                }
                case LESS_EQUAL -> {
                    return new LessEqualFilterCriterion(propertyName, property, new PropertyComparator(true));
                }
                case GREATER -> {
                    return new GreaterFilterCriterion(propertyName, property, new PropertyComparator(false));
                }
                case GREATER_EQUAL -> {
                    return new GreaterEqualFilterCriterion(propertyName, property, new PropertyComparator(false));
                }
            }
        }

        switch (predicate) {
            case CONTAINS -> {
                if (propertyDescriptor.isCollection()) {
                    return new CollectionContainsFilterCriterion(propertyName, operand);
                } else if (propertyDescriptor.type() == PropertyDescriptor.Type.String) {
                    if (operand.size() != 1) {
                        throw new TaskServiceException("one argument expected for predicate 'contains' of property '" + propertyName + "'");
                    }
                    return new ContainsCaseInsensitiveFilterCriterion(propertyName, (String) operand.get(0));
                } else {
                    throw new TaskServiceException("predicate 'contains' is incompatible with property '" + propertyName + "'");
                }
            }
            case IN -> {
                return new InFilterCriterion(propertyName, operand);
            }
            case NULL -> {
                return new NullFilterCriterion(propertyName);
            }
            case EMPTY -> {
                return new EmptyFilterCriterion(propertyName);
            }
        }

        throw new RuntimeException("predicate '" + predicate + "' not implemented");
    }

    private FilterCriterion createFilterCriterion(@NonNull FilterCriterionInfo filterCriterionInfo, @NonNull PropertyManager propertyManager) throws PropertyException, IOException, PropertyConverterException, TaskServiceException {
        switch (filterCriterionInfo.type()) {
            case PROPERTY -> {
                PropertyDescriptor propertyDescriptor = propertyManager.getPropertyDescriptor(filterCriterionInfo.propertyName());
                List<Object> operand = null;
                if (filterCriterionInfo.operands() != null) {
                    operand = propertyConverter.convertProperty(propertyDescriptor, filterCriterionInfo.operands());
                }
                return createFilterCriterion(propertyDescriptor, filterCriterionInfo.predicate(), operand);
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
            default ->
                    throw new NotImplementedException(filterCriterionInfo.type() + " filter criterion is not yet supported");
        }
    }

    private TaskRepository taskRepository;
    private PropertyManager propertyManager;
    private UUIDGenerator uuidGenerator;
    private PropertyConverter propertyConverter;
    private TempIDMappingService tempIDMappingService;

}

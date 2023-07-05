package task_manager.logic.use_case.task;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.NotImplementedException;
import task_manager.core.data.FilterCriterionInfo;
import task_manager.core.data.SortingInfo;
import task_manager.core.data.Task;
import task_manager.core.property.*;
import task_manager.core.repository.TaskRepository;
import task_manager.core.util.UUIDGenerator;
import task_manager.logic.PropertyComparator;
import task_manager.logic.PropertyNotComparableException;
import task_manager.logic.filter.*;
import task_manager.logic.filter.grammar.FilterBuilder;
import task_manager.logic.sorter.PropertySorter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class TaskUseCaseImpl implements TaskUseCase {

    @Override
    public Task addTask(Task task) throws IOException {
        task.getProperties().put("uuid", uuidGenerator.getUUID());
        return taskRepository.create(task);
    }

    @Override
    public Task modifyTask(Task task) throws IOException {
        return taskRepository.update(task);
    }

    @Override
    public boolean deleteTask(UUID uuid) throws IOException {
        return taskRepository.delete(uuid);
    }

    @Override
    public List<Task> getTasks() throws IOException {
        return taskRepository.getAll();
    }

    @Override
    public List<Task> getTasks(
            List<String> queries,
            List<FilterPropertySpec> propertySpecs,
            SortingInfo sortingInfo,
            FilterCriterionInfo filterCriterionInfo,
            List<UUID> taskUUIDs
    ) throws IOException, TaskUseCaseException, PropertyException, PropertyConverterException, FilterCriterionException {
        List<FilterCriterion> finalFilterCriteria = new ArrayList<>();
        PropertySorter<Task> sorter = null;

        List<Task> tasks;
        if (taskUUIDs != null) {
            tasks = new ArrayList<>();
            for(UUID taskUUID : taskUUIDs) {
                tasks.add(getTask(taskUUID));
            }
        } else {
            tasks = getTasks();
        }

        if (sortingInfo != null) {
            sorter = new PropertySorter<>(sortingInfo.sortingCriteria());
        }

        if (filterCriterionInfo != null) {
            finalFilterCriteria.add(createFilterCriterion(filterCriterionInfo, propertyManager));
        }

        if (queries != null) {
            for (String query : queries) {
                finalFilterCriteria.add(FilterBuilder.buildFilter(query));
            }
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

        if (finalFilterCriteria.size() != 0) {
            Filter filter = new SimpleFilter(new AndFilterCriterion(finalFilterCriteria));
            try {
                tasks = filter.doFilter(tasks, propertyManager);
            } catch (PropertyException e) {
                throw new TaskUseCaseException("Failed to filter tasks: " + e.getMessage());
            }
        }

        if (sorter != null) {
            try {
                tasks = sorter.sort(tasks, propertyManager);
            } catch (PropertyException | PropertyNotComparableException e) {
                throw new TaskUseCaseException("Failed to sort tasks: " + e.getMessage());
            }
        }

        return tasks;
    }

    @Override
    public Task getTask(UUID uuid) throws IOException {
        return taskRepository.get(uuid);
    }

    @Override
    public void deleteAllTasks() throws IOException {
        taskRepository.deleteAll();
    }

    private FilterCriterion getFilterCriterion(FilterPropertySpec propertySpec) throws TaskUseCaseException {
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
                        throw new TaskUseCaseException("Illegal type for CONTAINS predicate: "
                                + propertySpec.property().getPropertyDescriptor());
                    }
                } catch (PropertyException e) {
                    throw new TaskUseCaseException("Failed to filter tasks: " + e.getMessage());
                }
            }
            case LESS -> {
                if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                    throw new TaskUseCaseException("Illegal type for LESS predicate: "
                            + propertySpec.property().getPropertyDescriptor());
                }
                return new LessFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                        propertySpec.property(), new PropertyComparator(true));
            }
            case LESS_EQUAL -> {
                if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                    throw new TaskUseCaseException("Illegal type for LESS_EQUAL predicate: "
                            + propertySpec.property().getPropertyDescriptor());
                }
                return new LessEqualFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                        propertySpec.property(), new PropertyComparator(true));
            }
            case GREATER -> {
                if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                    throw new TaskUseCaseException("Illegal type for GREATER predicate: "
                            + propertySpec.property().getPropertyDescriptor());
                }
                return new GreaterFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                        propertySpec.property(), new PropertyComparator(false));
            }
            case GREATER_EQUAL -> {
                if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                    throw new TaskUseCaseException("Illegal type for GREATER_EQUAL predicate: "
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

    private final TaskRepository taskRepository;
    private final PropertyManager propertyManager;
    private final UUIDGenerator uuidGenerator;
    private final PropertyConverter propertyConverter;

}

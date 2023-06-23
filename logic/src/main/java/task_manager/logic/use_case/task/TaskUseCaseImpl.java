package task_manager.logic.use_case.task;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.NotImplementedException;
import task_manager.core.data.Predicate;
import task_manager.core.data.SortingCriterion;
import task_manager.core.data.Task;
import task_manager.core.property.PropertyDescriptor;
import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertyManager;
import task_manager.core.property.PropertySpec;
import task_manager.core.repository.TaskRepository;
import task_manager.core.util.UUIDGenerator;
import task_manager.logic.PropertyComparator;
import task_manager.logic.PropertyNotComparableException;
import task_manager.logic.filter.*;
import task_manager.logic.filter.grammar.FilterBuilder;
import task_manager.logic.sorter.PropertySorter;
import task_manager.logic.use_case.view.PropertyConverterException;
import task_manager.logic.use_case.view.View;
import task_manager.logic.use_case.view.ViewUseCase;

import java.io.IOException;
import java.util.ArrayList;
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
    public List<Task> getTasks(String nameQuery, List<String> queries, List<PropertySpec> propertySpecs, List<SortingCriterion> sortingCriteria, String viewName)
            throws IOException, TaskUseCaseException {
        List<Task> tasks = getTasks();
        List<FilterCriterion> finalFilterCriteria = new ArrayList<>();
        PropertySorter<Task> sorter = null;

        if (viewName != null) {
            View view = getView(viewName);
            sorter = view.sorter();
            if (view.filterCriterion() != null) {
                finalFilterCriteria.add(view.filterCriterion());
            }
        }

        if (queries != null) {
            for (String query : queries) {
                finalFilterCriteria.add(FilterBuilder.buildFilter(query));
            }
        }

        if (nameQuery != null) {
            finalFilterCriteria.add(new ContainsCaseInsensitiveFilterCriterion("name", nameQuery));
        }

        if (propertySpecs != null) {
            for (PropertySpec propertySpec : propertySpecs) {
                FilterCriterion filterCriterion = getFilterCriterion(propertySpec);

                if (propertySpec.affinity() == PropertySpec.Affinity.NEGATIVE) {
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

        if (sortingCriteria != null && !sortingCriteria.isEmpty()) {
            sorter = new PropertySorter<>(sortingCriteria);
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

    private @NonNull View getView(String viewName) throws TaskUseCaseException, IOException {
        try {
            View view = viewUseCase.getView(viewName, propertyManager);
            if (view == null) {
                throw new TaskUseCaseException("View '" + viewName + "' does not exist");
            }
            return view;
        } catch (PropertyException | PropertyConverterException | FilterCriterionException e) {
            throw new TaskUseCaseException("Failed to get view '" + viewName + "': " + e.getMessage());
        }
    }


    private FilterCriterion getFilterCriterion(PropertySpec propertySpec) throws TaskUseCaseException {
        if (propertySpec.predicate() == Predicate.CONTAINS) {
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
        } else if (propertySpec.predicate() == Predicate.LESS) {
            if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                throw new TaskUseCaseException("Illegal type for LESS predicate: "
                        + propertySpec.property().getPropertyDescriptor());
            }
            return new LessFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                    propertySpec.property(), new PropertyComparator(true));
        } else if (propertySpec.predicate() == Predicate.LESS_EQUAL) {
            if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                throw new TaskUseCaseException("Illegal type for LESS_EQUAL predicate: "
                        + propertySpec.property().getPropertyDescriptor());
            }
            return new LessEqualFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                    propertySpec.property(), new PropertyComparator(true));
        } else if (propertySpec.predicate() == Predicate.GREATER) {
            if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                throw new TaskUseCaseException("Illegal type for GREATER predicate: "
                        + propertySpec.property().getPropertyDescriptor());
            }
            return new GreaterFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                    propertySpec.property(), new PropertyComparator(false));
        } else if (propertySpec.predicate() == Predicate.GREATER_EQUAL) {
            if (!PropertyComparator.isComparable(propertySpec.property().getPropertyDescriptor())) {
                throw new TaskUseCaseException("Illegal type for GREATER_EQUAL predicate: "
                        + propertySpec.property().getPropertyDescriptor());
            }
            return new GreaterEqualFilterCriterion(propertySpec.property().getPropertyDescriptor().name(),
                    propertySpec.property(), new PropertyComparator(false));
        } else if (propertySpec.predicate() == null) {
            return new EqualFilterCriterion(
                    propertySpec.property().getPropertyDescriptor().name(),
                    propertySpec.property().getValue());
        } else {
            throw new NotImplementedException(propertySpec.predicate() + " predicate is not yet supported");
        }
    }

    private final TaskRepository taskRepository;
    private final ViewUseCase viewUseCase;
    private final PropertyManager propertyManager;
    private final UUIDGenerator uuidGenerator;

}

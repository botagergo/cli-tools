package task_manager.logic.use_case.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import task_manager.filter.*;
import task_manager.filter.grammar.FilterBuilder;
import task_manager.logic.use_case.view.PropertyConverterException;
import task_manager.logic.use_case.view.View;
import task_manager.logic.use_case.view.ViewUseCase;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyNotComparableException;
import task_manager.repository.TaskRepository;
import task_manager.data.Task;
import task_manager.sorter.PropertySorter;
import task_manager.util.UUIDGenerator;

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
    public List<Task> getTasks(String nameQuery, List<String> queries, List<FilterCriterion> filterCriteria, PropertySorter<Task> sorter, String viewName)
            throws IOException, TaskUseCaseException {
        List<Task> tasks = getTasks();
        List<FilterCriterion> finalFilterCriteria = new ArrayList<>();

        if (viewName != null) {
            View view = getView(viewName);
            if (sorter == null) {
                sorter = view.sorter();
            }
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

        if (filterCriteria != null) {
            finalFilterCriteria.addAll(filterCriteria);
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
            } catch (PropertyException|PropertyNotComparableException e) {
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
        } catch (PropertyException|PropertyConverterException|FilterCriterionException e) {
            throw new TaskUseCaseException("Failed to get view '" + viewName + "': " + e.getMessage());
        }
    }

    private final TaskRepository taskRepository;
    private final ViewUseCase viewUseCase;
    private final PropertyManager propertyManager;
    private final UUIDGenerator uuidGenerator;

}

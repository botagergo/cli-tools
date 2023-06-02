package task_manager.logic.use_case;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.filter.*;
import task_manager.filter.grammar.FilterBuilder;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.repository.TaskRepository;
import task_manager.data.Task;
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
    public List<Task> getTasks(String nameQuery, List<String> queries) throws IOException, PropertyException {
        List<Task> tasks = getTasks();
        List<FilterCriterion> filterCriteria = new ArrayList<>();

        if (queries != null) {
            for (String query : queries) {
                filterCriteria.add(FilterBuilder.buildFilter(query));
            }
        }

        if (nameQuery != null) {
            filterCriteria.add(new ContainsCaseInsensitiveFilterCriterion("name", nameQuery));
        }

        if (filterCriteria.size() != 0) {
            Filter filter = new SimpleFilter(new AndFilterCriterion(filterCriteria));
            tasks = filter.doFilter(tasks, propertyManager);
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

    private final TaskRepository taskRepository;
    private final PropertyManager propertyManager;
    private final UUIDGenerator uuidGenerator;

}

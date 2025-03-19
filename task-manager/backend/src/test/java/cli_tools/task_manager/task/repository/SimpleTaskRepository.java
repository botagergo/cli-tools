package common.task.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import cli_tools.task_manager.task.Task;
import cli_tools.task_manager.task.repository.TaskRepository;

import java.util.*;


@Setter
@Getter
@AllArgsConstructor
public class SimpleTaskRepository implements TaskRepository {

    public SimpleTaskRepository() {
        tasks = new ArrayList<>();
    }

    @Override
    public Task create(@NonNull Task task) {
        tasks.add(task);
        return task;
    }

    @Override
    public Task get(@NonNull UUID uuid) {
        for (Task task : tasks) {
            if (Objects.equals(uuid, task.getUUID())) {
                return task;
            }
        }
        return null;
    }

    @Override
    public List<Task> getAll() {
        return tasks;
    }

    @Override
    public Task update(@NonNull UUID taskUuid, @NonNull Task task) {
        Optional<Task> taskToUpdateOptional = tasks.stream().filter(t -> t.getUUID() == taskUuid).findFirst();
        if (taskToUpdateOptional.isEmpty()) {
            return null;
        }

        Task taskToUpdate = taskToUpdateOptional.get();
        for (Map.Entry<String, Object> pair : task.getProperties().entrySet()) {
            taskToUpdate.getProperties().put(pair.getKey(), pair.getValue());
        }

        return taskToUpdate;
    }

    @Override
    public boolean delete(@NonNull UUID uuid) {
        for (Task task : tasks) {
            if (task.getUUID().equals(uuid)) {
                tasks.remove(task);
                return true;
            }
        }

        return false;
    }

    @Override
    public void deleteAll() {
        tasks.clear();
    }

    private List<Task> tasks;

}

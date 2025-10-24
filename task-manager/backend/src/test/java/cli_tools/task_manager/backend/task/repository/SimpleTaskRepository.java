package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.util.UUIDGenerator;
import cli_tools.task_manager.backend.task.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;


@Setter
@Getter
@AllArgsConstructor
public class SimpleTaskRepository implements TaskRepository {

    private final UUIDGenerator uuidGenerator;
    private List<Task> tasks;

    public SimpleTaskRepository(UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
        tasks = new ArrayList<>();
    }

    @Override
    public @NonNull Task create(@NonNull Task task) {
        task.getProperties().put(Task.UUID

, uuidGenerator.getUUID());
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
    public @NonNull List<Task> getAll() {
        return tasks;
    }

    @Override
    public Task update(@NonNull Task task) {
        Optional<Task> taskToUpdateOptional = tasks.stream().filter(t -> t.getUUID() == task.getUUID()).findFirst();
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
    public Task delete(@NonNull UUID uuid) {
        for (Task task : tasks) {
            if (task.getUUID().equals(uuid)) {
                tasks.remove(task);
                return task;
            }
        }

        return null;
    }

    @Override
    public void deleteAll() {
        tasks.clear();
    }

}

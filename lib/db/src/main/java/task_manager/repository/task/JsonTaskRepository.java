package task_manager.repository.task;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import task_manager.core.data.Task;
import task_manager.core.repository.TaskRepository;
import task_manager.repository.SimpleJsonRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Singleton
public class JsonTaskRepository extends SimpleJsonRepository<ArrayList<Task>> implements TaskRepository {

    @Inject
    public JsonTaskRepository(@Named("taskJsonFile") File jsonPath) {
        super(jsonPath);
        getObjectMapper().addMixIn(Task.class, TaskMixIn.class);
    }

    @Override
    public @NonNull Task create(@NonNull Task task) throws IOException, IllegalArgumentException {
        List<Task> tasks = getData();

        tasks.add(task);
        writeData();

        return task;
    }

    @Override
    public Task get(@NonNull UUID uuid) throws IOException {
        ArrayList<Task> tasks = getData();

        for (Task task : tasks) {
            if (Objects.equals(uuid, task.getUUID())) {
                return task;
            }
        }

        return null;
    }

    @Override
    public List<Task> getAll() throws IOException {
        return getData();
    }

    @Override
    public Task update(@NonNull UUID taskUuid, @NonNull Task task) throws IOException {
        ArrayList<Task> tasks = getData();

        Optional<Task> taskToUpdateOptional = tasks.stream().filter(t -> t.getUUID() == taskUuid).findFirst();
        if (taskToUpdateOptional.isEmpty()) {
            return null;
        }

        Task taskToUpdate = taskToUpdateOptional.get();
        for (Map.Entry<String, Object> pair : task.getProperties().entrySet()) {
            taskToUpdate.getProperties().put(pair.getKey(), pair.getValue());
        }

        writeData();
        return taskToUpdate;
    }

    @Override
    public boolean delete(@NonNull UUID uuid) throws IOException {
        ArrayList<Task> tasks = getData();

        for (Task task : tasks) {
            if (task.getUUID().equals(uuid)) {
                tasks.remove(task);
                writeData();
                return true;
            }
        }

        return false;
    }

    @Override
    public void deleteAll() throws IOException {
        getData().clear();
        writeData();
    }

    @Override
    public ArrayList<Task> getEmptyData() {
        return new ArrayList<>();
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(ArrayList.class, Task.class);
    }
}

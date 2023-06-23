package task_manager.repository.task;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import task_manager.data.Task;
import task_manager.repository.SimpleJsonRepository;
import task_manager.repository.TaskRepository;

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
    public Task create(Task task) throws IOException, IllegalArgumentException {
        List<Task> tasks = getData();

        tasks.add(task);
        writeData();

        return task;
    }

    @Override
    public Task get(UUID uuid) throws IOException {
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
    public Task update(Task task) throws IOException {
        ArrayList<Task> tasks = getData();

        Task taskToUpdate = null;
        for (Task taskToUpdate_ : tasks) {
            if (Objects.equals(taskToUpdate_.getUUID(), task.getUUID())) {
                taskToUpdate = taskToUpdate_;
                break;
            }
        }
        if (taskToUpdate == null) {
            return null;
        }
        for (Map.Entry<String, Object> pair : task.getProperties().entrySet()) {
            if (!Objects.equals(pair.getKey(), "uuid")) {
                taskToUpdate.getProperties().put(pair.getKey(), pair.getValue());
            }
        }
        writeData();
        return taskToUpdate;

    }

    @Override
    public boolean delete(UUID uuid) throws IOException {
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

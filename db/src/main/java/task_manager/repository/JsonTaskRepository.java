package task_manager.repository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import task_manager.data.Task;

@Singleton
public class JsonTaskRepository implements TaskRepository {

    @Inject
    public JsonTaskRepository(@Named("basePath") File basePath) {
        final String jsonFileName = "task.json";
        this.basePath = basePath;
        this.jsonFile = new File(basePath, jsonFileName);
    }

    @Override
    public Task create(Task task) throws IOException, IllegalArgumentException {
        if (tasks == null) {
            tasks = getTasks();
        }

        tasks.add(task);
        JsonMapper.writeJson(jsonFile, tasks.stream().map(Task::getRawProperties).collect(Collectors.toList()));
        return task;
    }

    @Override
    public Task get(UUID uuid) throws IOException {
        if (tasks == null) {
            tasks = getTasks();
        }

        for (Task task : tasks) {
            if (Objects.equals(uuid, task.getUUID())) {
                return task;
            }
        }

        return null;
    }

    @Override
    public List<Task> getAll() throws IOException {
        if (tasks == null) {
            tasks = getTasks();
        }
        return tasks;
    }

    @Override
    public Task update(Task task) throws IOException {
        if (tasks == null) {
            tasks = getTasks();
        }

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
        for (Map.Entry<String, Object> pair : task.getRawProperties().entrySet()) {
            if (!Objects.equals(pair.getKey(), "uuid")) {
                taskToUpdate.getRawProperties().put(pair.getKey(), pair.getValue());
            }
        }
        writeTasks(tasks);
        return taskToUpdate;

    }

    @Override
    public boolean delete(UUID uuid) throws IOException {
        if (tasks == null) {
            tasks = getTasks();
        }

        for (Task task : tasks) {
            if (task.getUUID().equals(uuid)) {
                tasks.remove(task);
                writeTasks(tasks);
                return true;
            }
        }

        return false;
    }

    @Override
    public void deleteAll() throws IOException {
        writeTasks(List.of());
    }

    private void writeTasks(List<Task> tasks) throws IOException {
        if (!basePath.exists()) {
            //noinspection ResultOfMethodCallIgnored
            basePath.mkdirs();
        }

        List<HashMap<String, Object>> converted_tasks =
            tasks.stream().map(Task::getRawProperties).collect(Collectors.toList());
        JsonMapper.writeJson(jsonFile, converted_tasks);
    }

    private List<Task> getTasks() throws IOException {
        if (!basePath.exists()) {
            //noinspection ResultOfMethodCallIgnored
            basePath.mkdirs();
        }

        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();
        Iterator<HashMap<String, Object>> taskIter = JsonMapper.readJson(jsonFile).stream().iterator();
        while (taskIter.hasNext()) {
            tasks.add(Task.fromMap(taskIter.next()));
        }

        return tasks;
    }

    private List<Task> tasks = null;
    private final File basePath;
    private final File jsonFile;


}

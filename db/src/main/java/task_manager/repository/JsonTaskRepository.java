package task_manager.repository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.lang3.tuple.Pair;
import task_manager.data.property.Property;
import task_manager.data.property.PropertyException;
import task_manager.data.Task;

public class JsonTaskRepository implements TaskRepository {

    @Inject
    public JsonTaskRepository(@Named("basePath") File basePath) {
        final String jsonFileName = "tasks.json";
        this.basePath = basePath;
        this.jsonFile = new File(basePath, jsonFileName);
    }

    @Override
    public Task create(Task task) throws IOException, IllegalArgumentException {
        if (tasks == null) {
            tasks = getTasks();
        }

        tasks.add(task);
        JsonMapper.writeJson(jsonFile, tasks.stream().map(Task::asMap).collect(Collectors.toList()));
        return task;
    }

    @Override
    public Task get(UUID uuid) throws IOException {
        if (tasks == null) {
            tasks = getTasks();
        }

        for (Task task : tasks) {
            try {
                if (task.getUuid().equals(uuid)) {
                    return task;
                }
            } catch (PropertyException e) {
                throw new IOException();
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

        try {
            Task taskToUpdate = null;

            for (Task taskToUpdate_ : tasks) {
                if (taskToUpdate_.getUuid().equals(task.getUuid())) {
                    taskToUpdate = taskToUpdate_;
                    break;
                }
            }

            if (taskToUpdate == null) {
                return null;
            }

            for (Pair<String, Property> pair : task.getPropertiesIter()) {
                if (!Objects.equals(pair.getKey(), "uuid")) {
                    taskToUpdate.setProperty(pair.getKey(), pair.getValue().getValue());
                }
            }

            writeTasks(tasks);
            return taskToUpdate;
        } catch (PropertyException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public boolean delete(UUID uuid) throws IOException {
        if (tasks == null) {
            tasks = getTasks();
        }

        try {
            for (Task task : tasks) {
                if (task.getUuid().equals(uuid)) {
                    tasks.remove(task);
                    writeTasks(tasks);
                    return true;
                }
            }
        } catch (PropertyException e) {
            throw new IOException();
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
            tasks.stream().map(Task::asMap).collect(Collectors.toList());
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

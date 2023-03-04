package task_manager.db;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JsonTaskRepository extends JsonRepository implements TaskRepository {

    public JsonTaskRepository(File basePath) {
        super(new File(basePath, jsonFileName));
    }

    @Override
    public Task addTask(Task task) throws IOException {
        List<Map<String, Object>> tasks = readJson();
        task.setUuid(UUID.randomUUID());
        tasks.add(task.asMap());
        writeJson(tasks);
        return task;
    }

    @Override
    public Task modifyTask(Task task)
            throws IOException, IllegalArgumentException {
        List<Map<String, Object>> tasks = readJson();
        Optional<Map<String, Object>> taskToUpdate = tasks.stream().filter(t -> {
            return ((UUID) t.get("uuid")).equals(task.getUuid());
        }).findFirst();

        if (taskToUpdate.isEmpty()) {
            throw new IllegalArgumentException("No such task: " + (String) task.getName());
        }

        taskToUpdate.get().put("done", task.getDone());
        writeJson(tasks);
        return Task.fromMap(taskToUpdate.get());
    }

    @Override
    public List<Task> getTasks() throws IOException {
        return readJson().stream().map(taskMap -> Task.fromMap(taskMap)).collect(Collectors.toList());
    }

    @Override
    public void deleteAllTasks() throws IOException {
        writeJson(List.of());
    }

    private static String jsonFileName = "tasks.json";

}

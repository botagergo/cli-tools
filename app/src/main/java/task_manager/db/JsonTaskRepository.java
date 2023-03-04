package task_manager.db;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JsonTaskRepository extends JsonRepository implements TaskRepository {

    public JsonTaskRepository(File basePath) {
        super(new File(basePath, jsonFileName));
    }

    @Override
    public Map<String, Object> addTask(Map<String, Object> task) throws IOException {
        List<Map<String, Object>> tasks = readJson();
        task.put("done", false);
        task.put("uuid", UUID.randomUUID().toString());
        tasks.add(task);
        writeJson(tasks);
        return task;
    }

    @Override
    public Map<String, Object> modifyTask(Map<String, Object> task)
            throws IOException, IllegalArgumentException {
        List<Map<String, Object>> tasks = readJson();
        Optional<Map<String, Object>> taskToUpdate = tasks.stream().filter(t -> {
            return ((String) t.get("uuid")).equals(task.get("uuid"));
        }).findFirst();

        if (taskToUpdate.isEmpty()) {
            throw new IllegalArgumentException("No such task: " + (String) task.get("name"));
        }

        taskToUpdate.get().put("done", task.get("done"));
        writeJson(tasks);
        return taskToUpdate.get();
    }

    @Override
    public List<Map<String, Object>> getTasks() throws IOException {
        return readJson();
    }

    private static String jsonFileName = "tasks.json";

}

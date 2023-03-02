package task_manager.db;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class JsonTaskRepository implements TaskRepository {

    public JsonTaskRepository(File basePath) {
        jsonPath = new File(basePath, jsonFileName);
        objectMapper = new ObjectMapper();
    }

    @Override
    public Map<String, Object> addTask(Map<String, Object> task) throws IOException {
        List<Map<String, Object>> tasks = readJson();
        tasks.add(task);
        writeJson(tasks);
        return task;
    }

    @Override
    public Map<String, Object> modifyTask(Map<String, Object> task) throws IOException {
        List<Map<String, Object>> tasks = readJson();
        Optional<Map<String, Object>> taskToUpdate = tasks.stream().filter(
                t -> {
                    return ((String) t.get("name")).equals(task.get("name"));
                }).findFirst();

        if (taskToUpdate.isEmpty()) {
            throw new IOException("No such task: " + (String) task.get("name"));
        }

        taskToUpdate.get().put("done", task.get("done"));
        writeJson(tasks);
        return taskToUpdate.get();
    }

    @Override
    public List<Map<String, Object>> getTasks() throws IOException {
        return readJson();
    }

    private List<Map<String, Object>> readJson() throws IOException {
        if (jsonPath.exists()) {
            return objectMapper.readValue(jsonPath, new TypeReference<>() {

            });
        } else {
            return new ArrayList<Map<String, Object>>();
        }
    }

    private void writeJson(List<Map<String, Object>> tasks) throws IOException {
        String json = objectMapper.writeValueAsString(tasks);
        FileWriter writer = new FileWriter(jsonPath);
        writer.write(json);
        writer.close();
    }

    private File jsonPath;
    private ObjectMapper objectMapper;

    private static String jsonFileName = "tasks.json";

}

package task_manager.repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public abstract class JsonMapper {

    public static List<HashMap<String, Object>> readJson(File jsonPath) throws IOException {
        if (jsonPath.exists()) {
            return objectMapper.readValue(jsonPath, new TypeReference<>() {

            });
        } else {
            return new ArrayList<>();
        }
    }

    public static <T> HashMap<String, T> readJsonMap(File jsonPath) throws IOException {
        if (jsonPath.exists()) {
            return objectMapper.readValue(jsonPath, new TypeReference<>() {

            });
        } else {
            return new HashMap<>();
        }
    }

    public static void writeJson(File jsonPath, List<HashMap<String, Object>> tasks) throws IOException {
        String json = objectMapper.writeValueAsString(tasks);
        FileWriter writer = new FileWriter(jsonPath);
        writer.write(json);
        writer.close();
    }

    public static <T> void writeJsonMap(File jsonPath, Map<String, T> tasks) throws IOException {
        String json = objectMapper.writeValueAsString(tasks);
        FileWriter writer = new FileWriter(jsonPath);
        writer.write(json);
        writer.close();
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

}

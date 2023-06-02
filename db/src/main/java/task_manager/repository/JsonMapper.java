package task_manager.repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.module.SimpleModule;

public abstract class JsonMapper {

    private static final ObjectMapper objectMapper;
    private static final ObjectMapper simpleObjectMapper;
    private static final SimpleModule module;

    static {
        objectMapper = new ObjectMapper();
        simpleObjectMapper = new ObjectMapper();
        module = new SimpleModule();
        module.addSerializer(new MapSerializer());
        module.addDeserializer(HashMap.class, new MapDeserializer());
        objectMapper.registerModule(module);
    }

    public static List<HashMap<String, Object>> readTaskJson(File jsonPath) throws IOException {
        if (jsonPath.exists()) {
                return objectMapper.readValue(jsonPath, new TypeReference<>() {

            });
        } else {
            return new ArrayList<>();
        }
    }

    public static List<HashMap<String, Object>> readJson(File jsonPath) throws IOException {
        if (jsonPath.exists()) {
            return simpleObjectMapper.readValue(jsonPath, new TypeReference<>() {

            });
        } else {
            return new ArrayList<>();
        }
    }

    public static <T> HashMap<String, T> readJsonMap(File jsonPath) throws IOException {
        if (jsonPath.exists()) {
            return simpleObjectMapper.readValue(jsonPath, new TypeReference<>() {

            });
        } else {
            return new HashMap<>();
        }
    }

    public static void writeTaskJson(File jsonPath, List<HashMap<String, Object>> tasks) throws IOException {
        String json = objectMapper.writeValueAsString(tasks);
        FileWriter writer = new FileWriter(jsonPath);
        writer.write(json);
        writer.close();
    }

    public static void writeJson(File jsonPath, List<HashMap<String, Object>> tasks) throws IOException {
        String json = simpleObjectMapper.writeValueAsString(tasks);
        FileWriter writer = new FileWriter(jsonPath);
        writer.write(json);
        writer.close();
    }

    public static <T> void writeJsonMap(File jsonPath, Map<String, T> tasks) throws IOException {
        String json = simpleObjectMapper.writeValueAsString(tasks);
        FileWriter writer = new FileWriter(jsonPath);
        writer.write(json);
        writer.close();
    }


}

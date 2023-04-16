package task_manager.db;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public abstract class JsonRepository {

    public JsonRepository(File jsonPath) {
        this.jsonPath = jsonPath;
        this.objectMapper = new ObjectMapper();
    }

    protected List<HashMap<String, Object>> readJson() throws IOException {
        if (jsonPath.exists()) {
            return objectMapper.readValue(jsonPath, new TypeReference<>() {

            });
        } else {
            return new ArrayList<HashMap<String, Object>>();
        }
    }

    protected void writeJson(List<HashMap<String, Object>> tasks) throws IOException {
        String json = objectMapper.writeValueAsString(tasks);
        FileWriter writer = new FileWriter(jsonPath);
        writer.write(json);
        writer.close();
    }

    private File jsonPath;
    private ObjectMapper objectMapper;
}

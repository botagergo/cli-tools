package task_manager.repository;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

public class JsonRepository <T> {

    public JsonRepository(File jsonFile) {
        this.jsonFile = jsonFile;
        this.objectMapper = new ObjectMapper();
    }

    public T getData() throws IOException {
        if (data == null) {
            data = loadData();
        }

        return data;
    }

    public T getEmptyData() {
        return null;
    }

    public void writeData() throws IOException {
        try {
            if (!jsonFile.exists()) {
                File parent = jsonFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    parent.mkdirs();
                }
            }
            objectMapper.writeValue(jsonFile, data);
        } catch (IOException e) {
            throw new IOException("Failed to write JSON file: " + jsonFile, e);
        }
    }

    protected JavaType constructType(TypeFactory typeFactory) {
        return null;
    }

    protected T loadData() throws IOException {
        try {
            if (jsonFile.exists()) {
                JavaType javaType = constructType(objectMapper.getTypeFactory());
                if (javaType != null) {
                    data = objectMapper.readValue(jsonFile, javaType);
                } else {
                    data = objectMapper.readValue(jsonFile, new TypeReference<>() {
                    });
                }
            } else {
                data = getEmptyData();
            }
        } catch (StreamReadException e) {
            throw new IOException("Failed to parse JSON file: " + jsonFile, e);
        } catch (DatabindException e) {
            throw new IOException("JSON file contains invalid data: " + jsonFile, e);
        } catch (IOException e) {
            throw new IOException("Failed to read JSON file: " + jsonFile, e);
        }

        if (data == null) {
            throw new IOException("JSON contains null: " + jsonFile);
        }

        return data;
    }

    private final File jsonFile;
    @Getter private final ObjectMapper objectMapper;
    private T data = null;
}

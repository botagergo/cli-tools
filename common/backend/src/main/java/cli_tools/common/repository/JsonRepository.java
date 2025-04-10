package cli_tools.common.repository;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.File;
import java.io.IOException;

public abstract class JsonRepository<T_Json, T_Stored> {

    private static final ObjectMapper objectMapper;
    private T_Stored data = null;
    private final File jsonFile;
    private ObjectWriter objectWriter;
    private ObjectReader objectReader;

    public JsonRepository(File jsonFile) {
        this.jsonFile = jsonFile;
    }

    static {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public T_Stored getData() throws IOException {
        if (data == null) {
            data = jsonToStoredData(loadData());
        }
        return data;
    }

    protected JavaType constructType(TypeFactory typeFactory) {
        return null;
    }
    protected TypeReference<T_Json> getTypeReference() {
        return null;
    }

    protected T_Json getEmptyData() {
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

            getObjectWriter().writeValue(jsonFile, storedToJsonData(data));
        } catch (IOException e) {
            throw new IOException("Failed to write JSON file: " + jsonFile, e);
        }
    }

    protected T_Json loadData() throws IOException {
        T_Json data;
        try {
            if (jsonFile.exists()) {
                data = getObjectReader().readValue(jsonFile);
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

    protected abstract T_Stored jsonToStoredData(T_Json data);

    protected abstract T_Json storedToJsonData(T_Stored data);

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private ObjectReader getObjectReader() {
        if (objectReader == null) {
            JavaType javaType = constructType(getObjectMapper().getTypeFactory());
            if (javaType != null) {
                objectReader = getObjectMapper().readerFor(javaType);
            } else {
                objectReader = getObjectMapper().readerFor(new TypeReference<>() {});
            }
        }
        return objectReader;
    }

    private ObjectWriter getObjectWriter() {
        if (objectWriter == null) {
            TypeReference<T_Json> typeReference = getTypeReference();
            if (typeReference != null) {
                objectWriter = getObjectMapper().writerFor(typeReference);
            } else {
                objectWriter = getObjectMapper().writer();
            }
        }
        return objectWriter;
    }

}

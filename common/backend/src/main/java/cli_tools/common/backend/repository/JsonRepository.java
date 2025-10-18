package cli_tools.common.backend.repository;

import cli_tools.common.core.repository.DataAccessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

public abstract class JsonRepository<T_Json, T_Stored> {

    private final ObjectMapper objectMapper;
    @Getter
    private final File jsonFile;
    private T_Stored data = null;
    private ObjectWriter objectWriter;
    private ObjectReader objectReader;

    public JsonRepository(File jsonFile) {
        this.jsonFile = jsonFile;
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public T_Stored getData() throws DataAccessException {
        if (data == null) {
            T_Json jsonData;
            try {
                jsonData = loadData();
            } catch (IOException e) {
                throw new DataAccessException("Error reading JSON file: %s".formatted(jsonFile.getAbsolutePath()), e);
            }
            data = jsonToStoredData(jsonData);
            if (data == null) {
                throw new DataAccessException("JSON contains null: " + jsonFile.getAbsolutePath(), null);
            }
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

    public void writeData() throws DataAccessException {
        try {
            getObjectWriter().writeValue(jsonFile, storedToJsonData(data));
        } catch (IOException e) {
            throw new DataAccessException("Error writing JSON file: " + jsonFile.getAbsolutePath(), e);
        }
    }

    protected abstract T_Stored jsonToStoredData(T_Json data);

    protected abstract T_Json storedToJsonData(T_Stored data);

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected ObjectReader getObjectReader() {
        if (objectReader == null) {
            JavaType javaType = constructType(getObjectMapper().getTypeFactory());
            if (javaType != null) {
                objectReader = getObjectMapper().readerFor(javaType);
            } else {
                objectReader = getObjectMapper().readerFor(getTypeReference());
            }
        }
        return objectReader;
    }

    private T_Json loadData() throws IOException {
        if (jsonFile.exists() && jsonFile.length() > 0) {
            return getObjectReader().readValue(jsonFile);
        } else {
            return getEmptyData();
        }
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

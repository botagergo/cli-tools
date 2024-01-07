package task_manager.repository;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import task_manager.core.repository.StateRepository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class JsonStateRepository extends SimpleJsonRepository<HashMap<String, Object>> implements StateRepository {

    @Inject
    public JsonStateRepository(@Named("stateJsonFile") File jsonFile) {
        super(jsonFile);
    }

    @Override
    public Object getValue(String name) throws IOException {
        return getData().get(name);
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructMapType(HashMap.class, String.class, Object.class);
    }

    @Override
    public HashMap<String, Object> getEmptyData() {
        return new HashMap<>();
    }
}

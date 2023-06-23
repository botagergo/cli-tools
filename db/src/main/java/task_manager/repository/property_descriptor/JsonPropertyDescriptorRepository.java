package task_manager.repository.property_descriptor;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import task_manager.core.property.PropertyDescriptor;
import task_manager.core.repository.PropertyDescriptorRepository;
import task_manager.repository.SimpleJsonRepository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class JsonPropertyDescriptorRepository extends SimpleJsonRepository<HashMap<String, PropertyDescriptor>> implements PropertyDescriptorRepository {

    @Inject
    public JsonPropertyDescriptorRepository(@Named("propertyDescriptorJsonFile") File jsonFile) {
        super(jsonFile);
        getObjectMapper().addMixIn(PropertyDescriptor.class, PropertyDescriptorMixIn.class);
        getObjectMapper().addMixIn(PropertyDescriptor.Extra.class, ExtraMixIn.class);
    }

    @Override
    public PropertyDescriptor get(String name) throws IOException {
        return getData().getOrDefault(name, null);
    }

    @Override
    public List<PropertyDescriptor> getAll() throws IOException {
        return List.copyOf(getData().values());
    }

    @Override
    public void create(PropertyDescriptor propertyDescriptor) throws IOException {
        getData().put(propertyDescriptor.name(), propertyDescriptor);
        writeData();
    }

    @Override
    public HashMap<String, PropertyDescriptor> getEmptyData() {
        return new HashMap<>();
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructMapType(HashMap.class, String.class, PropertyDescriptor.class);
    }
}

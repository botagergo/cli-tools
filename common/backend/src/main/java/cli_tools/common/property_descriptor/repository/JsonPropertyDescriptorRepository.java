package cli_tools.common.property_descriptor.repository;

import cli_tools.common.repository.SimpleJsonRepository;
import cli_tools.common.temp_id_mapping.service.TempIDMappingService;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import cli_tools.common.core.repository.PropertyDescriptorRepository;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PseudoPropertyProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonPropertyDescriptorRepository extends SimpleJsonRepository<HashMap<String, PropertyDescriptor>> implements PropertyDescriptorRepository {

    @Inject
    public JsonPropertyDescriptorRepository(
            @Named("propertyDescriptorJsonFile") File jsonFile,
            TempIDMappingService tempIDMappingService,
            Class<?> pseudoPropertyProviderMixIn
    ) {
        super(jsonFile);
        getObjectMapper().addMixIn(PropertyDescriptor.class, PropertyDescriptorMixIn.class);
        getObjectMapper().addMixIn(PropertyDescriptor.Subtype.class, SubtypeMixIn.class);
        getObjectMapper().addMixIn(PseudoPropertyProvider.class, pseudoPropertyProviderMixIn);

        InjectableValues injectableValues = new InjectableValues.Std()
                .addValue(TempIDMappingService.class, tempIDMappingService);
        getObjectMapper().setInjectableValues(injectableValues);
    }

    @Override
    public PropertyDescriptor get(String name) throws IOException {
        return getData().getOrDefault(name, null);
    }

    @Override
    public List<PropertyDescriptor> find(String name) throws IOException {
        return getData().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(name))
                .map(Map.Entry::getValue)
                .toList();
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

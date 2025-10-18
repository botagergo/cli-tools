package cli_tools.common.backend.property_descriptor.repository;

import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.core.repository.PropertyDescriptorRepository;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PseudoPropertyProvider;
import cli_tools.common.backend.repository.SimpleJsonRepository;
import cli_tools.common.backend.temp_id_mapping.TempIDManager;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NonNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonPropertyDescriptorRepository extends SimpleJsonRepository<HashMap<String, PropertyDescriptor>> implements PropertyDescriptorRepository {

    @Inject
    public JsonPropertyDescriptorRepository(
            @Named("propertyDescriptorJsonFile") File jsonFile,
            TempIDManager tempIdManager,
            Class<?> pseudoPropertyProviderMixIn
    ) {
        super(jsonFile);
        getObjectMapper().addMixIn(PropertyDescriptor.class, PropertyDescriptorMixIn.class);
        getObjectMapper().addMixIn(PropertyDescriptor.Subtype.class, SubtypeMixIn.class);
        getObjectMapper().addMixIn(PseudoPropertyProvider.class, pseudoPropertyProviderMixIn);

        InjectableValues injectableValues = new InjectableValues.Std()
                .addValue(TempIDManager.class, tempIdManager);
        getObjectMapper().setInjectableValues(injectableValues);
    }

    @Override
    public void create(@NonNull PropertyDescriptor propertyDescriptor) throws DataAccessException {
        getData().put(propertyDescriptor.name(), propertyDescriptor);
        writeData();
    }

    @Override
    public PropertyDescriptor get(@NonNull String name) throws DataAccessException {
        return getData().getOrDefault(name, null);
    }

    @Override
    public @NonNull List<PropertyDescriptor> find(@NonNull String name) throws DataAccessException {
        return getData().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(name))
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    public @NonNull List<PropertyDescriptor> getAll() throws DataAccessException {
        return List.copyOf(getData().values());
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructMapType(HashMap.class, String.class, PropertyDescriptor.class);
    }

    @Override
    public HashMap<String, PropertyDescriptor> getEmptyData() {
        return new HashMap<>();
    }
}

package cli_tools.common.cli.property_to_string_converter;

import cli_tools.common.repository.SimpleJsonRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class JsonPropertyToStringConverterRepository extends SimpleJsonRepository<HashMap<String, PropertyToStringConverter>> {
    @Inject
    public JsonPropertyToStringConverterRepository(
            @Named("propertyToStringConverterJsonFile") File jsonFile,
            DefaultPropertyToStringConverter defaultPropertyToStringConverter) {
        super(jsonFile);

        InjectableValues injectableValues = new InjectableValues.Std()
                .addValue(DefaultPropertyToStringConverter.class, defaultPropertyToStringConverter);
        getObjectMapper().setInjectableValues(injectableValues);
    }

    public void add(String propertyName, PropertyToStringConverter propertyToStringConverter) throws IOException {
        getData().put(propertyName, propertyToStringConverter);
        writeData();
    }

    public PropertyToStringConverter get(String propertyName) throws IOException {
        return getData().get(propertyName);
    }

    @Override
    protected TypeReference<HashMap<String, PropertyToStringConverter>> getTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructMapType(HashMap.class, String.class, PropertyToStringConverter.class);
    }

    @Override
    protected HashMap<String, PropertyToStringConverter> getEmptyData() {
        return new HashMap<>();
    }

}

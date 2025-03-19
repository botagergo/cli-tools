package task_manager.cli_lib.property_to_string_converter;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.property_lib.Property;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class MainPropertyToStringConverter implements PropertyToStringConverter {
    @Override
    public String propertyToString(String propertyName, Property property) throws IOException {
        PropertyToStringConverter propertyToStringConverter = propertyToStringConverterRepository.get(propertyName);
        if (propertyToStringConverter != null) {
            return propertyToStringConverter.propertyToString(propertyName, property);
        } else {
            return defaultPropertyToStringConverter.propertyToString(propertyName, property);
        }
    }

    JsonPropertyToStringConverterRepository propertyToStringConverterRepository;
    DefaultPropertyToStringConverter defaultPropertyToStringConverter;
}

package cli_tools.common.cli.property_to_string_converter;

import cli_tools.common.backend.property_converter.PropertyConverterException;
import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.property_lib.Property;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class MainPropertyToStringConverter implements PropertyToStringConverter {
    JsonPropertyToStringConverterRepository propertyToStringConverterRepository;
    DefaultPropertyToStringConverter defaultPropertyToStringConverter;

    @Override
    public String propertyToString(String propertyName, Property property) throws PropertyConverterException {
        PropertyToStringConverter propertyToStringConverter;
        try {
            propertyToStringConverter = propertyToStringConverterRepository.get(propertyName);
        } catch (DataAccessException e) {
            throw new PropertyConverterException("Error while getting string converter for property '%s': %s"
                    .formatted(propertyName, e.getMessage()), e);
        }
        if (propertyToStringConverter != null) {
            return propertyToStringConverter.propertyToString(propertyName, property);
        } else {
            return defaultPropertyToStringConverter.propertyToString(propertyName, property);
        }
    }
}

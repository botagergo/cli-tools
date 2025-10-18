package cli_tools.common.cli.property_to_string_converter;

import cli_tools.common.backend.property_converter.PropertyConverterException;
import cli_tools.common.property_lib.Property;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "converter")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PatternPropertyToStringConverter.class, name = "Pattern"),
})
public interface PropertyToStringConverter {

    default String propertyToString(String propertyName, Property property) throws PropertyConverterException {
        return null;
    }
}

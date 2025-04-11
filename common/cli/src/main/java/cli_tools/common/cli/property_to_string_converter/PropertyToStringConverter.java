package cli_tools.common.cli.property_to_string_converter;

import cli_tools.common.property_lib.Property;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.IOException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "converter")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PatternPropertyToStringConverter.class, name = "Pattern"),
})
public interface PropertyToStringConverter {

    String propertyToString(String propertyName, Property property) throws IOException;
}

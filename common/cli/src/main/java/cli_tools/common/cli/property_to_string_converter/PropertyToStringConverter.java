package cli_tools.common.cli.property_to_string_converter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cli_tools.common.property_lib.Property;

import java.io.IOException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PatternPropertyToStringConverter.class, name = "PatternPropertyToStringConverter"),
})
public interface PropertyToStringConverter {

    String propertyToString(String propertyName, Property property) throws IOException;
}

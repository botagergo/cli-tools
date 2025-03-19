package cli_tools.common.cli.property_to_string_converter;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;

import cli_tools.common.property_lib.Property;

import java.io.IOException;

public class PatternPropertyToStringConverter implements PropertyToStringConverter {
    public PatternPropertyToStringConverter(
            @JacksonInject DefaultPropertyToStringConverter defaultPropertyToStringConverter,
            @JsonProperty("pattern") String pattern) {
        this.defaultPropertyToStringConverter = defaultPropertyToStringConverter;
        this.pattern = pattern;
    }

    @Override
    public String propertyToString(String propertyName, Property property) throws IOException {
        if (property.getValue() == null) {
            return "";
        } else {
            String str = defaultPropertyToStringConverter.propertyToString(propertyName, property);
            return pattern.formatted(str);
        }
    }
    private final DefaultPropertyToStringConverter defaultPropertyToStringConverter;
    @JsonProperty("pattern") private final String pattern;
}

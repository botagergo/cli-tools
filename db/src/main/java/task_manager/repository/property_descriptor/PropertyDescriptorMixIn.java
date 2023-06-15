package task_manager.repository.property_descriptor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import task_manager.property.ObjectDeserializer;
import task_manager.property.ObjectSerializer;
import task_manager.property.PropertyDescriptor;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public record PropertyDescriptorMixIn(
        @JsonSerialize @JsonDeserialize @JsonProperty(required = true) String name,
        @JsonSerialize @JsonDeserialize @JsonProperty(required = true) PropertyDescriptor.Type type,
        @JsonSerialize @JsonDeserialize @JsonProperty(required = true) PropertyDescriptor.Multiplicity multiplicity,
        @JsonSerialize(using = ObjectSerializer.class)
        @JsonDeserialize(using = ObjectDeserializer.class)
        @JsonProperty(required = true)
        Object defaultValue
) { }

package task_manager.repository.property_descriptor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import task_manager.core.property.PropertyDescriptor;
import task_manager.repository.ObjectDeserializer;
import task_manager.repository.ObjectSerializer;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PropertyDescriptorMixIn(
        @JsonProperty(required = true) String name,
        @JsonProperty(required = true) PropertyDescriptor.Type type,
        @JsonProperty PropertyDescriptor.Extra extra,
        @JsonProperty(required = true) PropertyDescriptor.Multiplicity multiplicity,
        @JsonSerialize(using = ObjectSerializer.class)
        @JsonDeserialize(using = ObjectDeserializer.class)
        @JsonProperty
        Object defaultValue
) { }

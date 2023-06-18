package task_manager.repository.property_descriptor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import task_manager.property.PropertyDescriptor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PropertyDescriptor.UUIDExtra.class, name = "UUIDExtra"),
        @JsonSubTypes.Type(value = PropertyDescriptor.StringExtra.class, name = "StringExtra")
})
public record ExtraMixIn() { }

package cli_tools.common.property_descriptor.repository;

import cli_tools.common.repository.ObjectDeserializer;
import cli_tools.common.repository.ObjectSerializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PseudoPropertyProvider;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PropertyDescriptorMixIn(
        @JsonProperty(required = true) String name,
        @JsonProperty(required = true) PropertyDescriptor.Type type,
        @JsonProperty PropertyDescriptor.Subtype subtype,
        @JsonProperty(required = true) PropertyDescriptor.Multiplicity multiplicity,
        @JsonSerialize(using = ObjectSerializer.class)
        @JsonDeserialize(using = ObjectDeserializer.class)
        @JsonProperty
        Object defaultValue,

        @JsonProperty PseudoPropertyProvider pseudoPropertyProvider
) { }

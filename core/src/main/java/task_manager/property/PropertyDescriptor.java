package task_manager.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public record PropertyDescriptor(
        @JsonProperty(required = true) String name,
        @JsonProperty(required = true) PropertyDescriptor.Type type,
        @JsonProperty(required = true) Multiplicity multiplicity,
        @JsonSerialize(using = ObjectSerializer.class)
        @JsonDeserialize(using = ObjectDeserializer.class)
        @JsonProperty(required = true)
        Object defaultValue
) {

    @JsonIgnore
    public boolean isCollection() {
        return multiplicity == Multiplicity.LIST || multiplicity == Multiplicity.SET;
    }

    @Override
    public String toString() {
        return type.toString() + " " + multiplicity;
    }

    public enum Type {
        String, UUID, Boolean
    }


    public enum Multiplicity {
        SINGLE,
        LIST,
        SET
    }

}

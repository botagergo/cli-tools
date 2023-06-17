package task_manager.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.util.List;

public record FilterCriterionInfo(
        String name,
        @JsonProperty(required = true) @NonNull Type type,
        String propertyName,
        List<FilterCriterionInfo> children,
        Predicate predicate,
        List<Object> operands
) {

    public enum Type {
        PROPERTY,
        AND,
        OR,
        NOT,
        REF
    }

    public enum Predicate {
        EQUALS,
        CONTAINS
    }

}

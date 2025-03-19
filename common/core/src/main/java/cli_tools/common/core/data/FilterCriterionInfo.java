package cli_tools.common.core.data;

import lombok.NonNull;

import java.util.List;

public record FilterCriterionInfo(
        String name,
        @NonNull Type type,
        String propertyName,
        List<FilterCriterionInfo> children,
        Predicate predicate,
        List<Object> operands
) {

    public enum Type {
        PROPERTY,
        AND,
        OR,
        NOT
    }

}

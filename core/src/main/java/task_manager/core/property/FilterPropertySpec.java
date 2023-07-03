package task_manager.core.property;

import lombok.NonNull;
import task_manager.core.data.Predicate;

public record FilterPropertySpec(
        @NonNull Property property,
        boolean negate,
        Predicate predicate) {

}
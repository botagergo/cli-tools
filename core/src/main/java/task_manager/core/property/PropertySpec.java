package task_manager.core.property;

import task_manager.core.data.Predicate;

public record PropertySpec(Property property, Affinity affinity, Predicate predicate) {

    public enum Affinity {
        NEGATIVE,
        NEUTRAL,
        POSITIVE
    }

}
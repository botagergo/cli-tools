package task_manager.property;

import task_manager.data.Predicate;

public record PropertySpec(Property property, Affinity affinity, Predicate predicate) {

    public enum Affinity {
        NEGATIVE,
        NEUTRAL,
        POSITIVE
    }

}
package task_manager.property;

public record PropertySpec(Property property, Affinity affinity, Predicate predicate) {

    public enum Affinity {
        NEGATIVE,
        NEUTRAL,
        POSITIVE
    }

    public enum Predicate {
        EQUALS,
        CONTAINS,
    }

}
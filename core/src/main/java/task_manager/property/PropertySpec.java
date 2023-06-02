package task_manager.property;

public record PropertySpec(Property property, Affinity affinity) {

    public enum Affinity {
        NEGATIVE,
        NEUTRAL,
        POSITIVE
    }

}
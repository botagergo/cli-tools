package task_manager.property;

public record PropertyDescriptor(
        String name,
        PropertyDescriptor.Type type,
        Multiplicity multiplicity,
        Object defaultValue
) {

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

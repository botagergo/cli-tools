package task_manager.property;

public record PropertyDescriptor(String name, PropertyDescriptor.Type type, boolean isList,
                                 Object defaultValue) {

    public enum Type {
        String, UUID, Boolean
    }
}

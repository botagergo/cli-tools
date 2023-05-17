package task_manager.data.property;

public record PropertyDescriptor(String name, task_manager.data.property.PropertyDescriptor.Type type, boolean isList,
                                 Object defaultValue) {

    public enum Type {
        String, UUID, Boolean
    }
}

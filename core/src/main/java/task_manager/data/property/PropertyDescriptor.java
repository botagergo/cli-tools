package task_manager.data.property;

public class PropertyDescriptor {

    public PropertyDescriptor(String name, Type type, boolean isList, Object defaultValue) {
        this.name = name;
        this.type = type;
        this.isList = isList;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean getIsList() {
        return isList;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    private final String name;
    private final Type type;
    private final boolean isList;
    private final Object defaultValue;

    public enum Type {
        String, UUID, Boolean
    }
}

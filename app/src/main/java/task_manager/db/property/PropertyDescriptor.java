package task_manager.db.property;

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

    private String name;
    private Type type;
    private boolean isList;
    private Object defaultValue;

    public enum Type {
        String, UUID, Boolean
    }
}

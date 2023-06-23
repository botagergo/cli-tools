package task_manager;

import task_manager.core.property.PropertyOwner;

import java.util.HashMap;

public class PropertyOwnerImpl implements PropertyOwner {

    public PropertyOwnerImpl(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public PropertyOwnerImpl() {
        this(new HashMap<>());
    }

    @Override
    public HashMap<String, Object> getProperties() {
        return properties;
    }

    private final HashMap<String, Object> properties;

}

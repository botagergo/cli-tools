package task_manager.property_lib;

import java.util.HashMap;

public class PropertyOwnerImpl extends PropertyOwner {

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

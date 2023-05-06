package task_manager;

import java.util.HashMap;
import task_manager.db.property.PropertyManager;
import task_manager.db.property.PropertyOwner;

class PropertyOwnerImpl extends PropertyOwner {

    public PropertyOwnerImpl(PropertyManager propertyManager, HashMap<String, Object> properties) {
        this.propertyManager = propertyManager;
        this.properties = properties;
    }

    public PropertyOwnerImpl(PropertyManager propertyManager) {
        this(propertyManager, new HashMap<>());
    }

    @Override
    public PropertyManager getPropertyManager() {
        return this.propertyManager;
    }

    @Override
    public HashMap<String, Object> getRawProperties() {
        return properties;
    }

    public HashMap<String, Object> properties;

    private PropertyManager propertyManager;
}

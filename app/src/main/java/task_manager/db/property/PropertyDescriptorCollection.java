package task_manager.db.property;

import java.util.HashMap;
import java.util.Map;

public class PropertyDescriptorCollection {

    public PropertyDescriptorCollection() {
        this.propertyDescriptors = new HashMap<>();
    }

    public void addPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        propertyDescriptors.put(propertyDescriptor.getName(), propertyDescriptor);
    }

    public PropertyDescriptor getPropertyDescriptor(String name) {
        if (propertyDescriptors.containsKey(name)) {
            return propertyDescriptors.get(name);
        } else {
            return null;
        }
    }

    public void clear() {
        propertyDescriptors.clear();
    }

    private Map<String, PropertyDescriptor> propertyDescriptors;

}

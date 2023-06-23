package task_manager.core.property;

import java.util.HashMap;

public class PropertyDescriptorCollection {

    public PropertyDescriptorCollection() {
        this.propertyDescriptors = new HashMap<>();
    }

    public void addPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        propertyDescriptors.put(propertyDescriptor.name(), propertyDescriptor);
    }

    public PropertyDescriptor get(String name) {
        return propertyDescriptors.getOrDefault(name, null);
    }

    public boolean isEmpty() {
        return propertyDescriptors.size() == 0;
    }

    private final HashMap<String, PropertyDescriptor> propertyDescriptors;

}

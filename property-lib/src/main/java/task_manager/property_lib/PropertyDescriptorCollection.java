package task_manager.property_lib;

import java.util.HashMap;
import java.util.List;

public class PropertyDescriptorCollection {

    public PropertyDescriptorCollection() {
        this.propertyDescriptors = new HashMap<>();
    }

    public static PropertyDescriptorCollection fromList(List<PropertyDescriptor> propertyDescriptors) {
        PropertyDescriptorCollection propertyDescriptorCollection = new PropertyDescriptorCollection();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            propertyDescriptorCollection.addPropertyDescriptor(propertyDescriptor);
        }
        return propertyDescriptorCollection;
    }

    public void addPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        propertyDescriptors.put(propertyDescriptor.name(), propertyDescriptor);
    }

    public void clear() {
        propertyDescriptors.clear();
    }

    public PropertyDescriptor get(String name) {
        return propertyDescriptors.getOrDefault(name, null);
    }

    public boolean isEmpty() {
        return propertyDescriptors.isEmpty();
    }

    private final HashMap<String, PropertyDescriptor> propertyDescriptors;

}

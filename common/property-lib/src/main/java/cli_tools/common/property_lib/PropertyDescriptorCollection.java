package cli_tools.common.property_lib;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyDescriptorCollection {

    private final HashMap<String, PropertyDescriptor> propertyDescriptors;

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

    public Map<String, PropertyDescriptor> getAll() {
        return Collections.unmodifiableMap(propertyDescriptors);
    }

    public void clear() {
        propertyDescriptors.clear();
    }

    public PropertyDescriptor get(String name) {
        return propertyDescriptors.getOrDefault(name, null);
    }

}

package task_manager.db.property;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PropertyDescriptorCollection {

    public PropertyDescriptorCollection(List<PropertyDescriptor> propertyDescriptors) {
        this.propertyDescriptors = propertyDescriptors.stream()
                .collect(Collectors.toMap(propertyDescriptor -> propertyDescriptor.getName(),
                        propertyDescriptor -> propertyDescriptor, (v1, v2) -> v1, HashMap::new));
    }

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

    private HashMap<String, PropertyDescriptor> propertyDescriptors;

}

package task_manager.data.property;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PropertyDescriptorCollection {

    public PropertyDescriptorCollection(List<PropertyDescriptor> propertyDescriptors) {
        this.propertyDescriptors = propertyDescriptors.stream()
                .collect(Collectors.toMap(PropertyDescriptor::name,
                        propertyDescriptor -> propertyDescriptor, (v1, v2) -> v1, HashMap::new));
    }

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

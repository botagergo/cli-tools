package task_manager.logic.use_case.property_descriptor;

import task_manager.core.property.PropertyDescriptor;
import task_manager.core.property.PropertyException;

import java.io.IOException;
import java.util.List;

public interface PropertyDescriptorUseCase {
    void createPropertyDescriptor(PropertyDescriptor propertyDescriptor) throws IOException;

    PropertyDescriptor getPropertyDescriptor(String name) throws IOException, PropertyException;

    List<PropertyDescriptor> getPropertyDescriptors() throws IOException;
}

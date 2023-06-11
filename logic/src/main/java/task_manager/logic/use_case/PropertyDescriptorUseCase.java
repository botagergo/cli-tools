package task_manager.logic.use_case;

import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyException;

import java.io.IOException;
import java.util.List;

public interface PropertyDescriptorUseCase {
    void createPropertyDescriptor(PropertyDescriptor propertyDescriptor) throws IOException;

    PropertyDescriptor getPropertyDescriptor(String name) throws IOException, PropertyException;

    List<PropertyDescriptor> getPropertyDescriptors() throws IOException;
}

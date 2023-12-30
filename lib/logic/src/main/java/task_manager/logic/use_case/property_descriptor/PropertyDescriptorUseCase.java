package task_manager.logic.use_case.property_descriptor;

import task_manager.property_lib.PropertyDescriptor;
import task_manager.property_lib.PropertyException;

import java.io.IOException;
import java.util.List;

public interface PropertyDescriptorUseCase {
    void createPropertyDescriptor(PropertyDescriptor propertyDescriptor) throws IOException;

    PropertyDescriptor getPropertyDescriptor(String name) throws IOException, PropertyException;

    PropertyDescriptor findPropertyDescriptor(String name) throws PropertyException, IOException;

    List<PropertyDescriptor> getPropertyDescriptors() throws IOException;
}

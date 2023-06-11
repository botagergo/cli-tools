package task_manager.repository;

import java.io.IOException;
import java.util.List;

import task_manager.property.PropertyDescriptor;

public interface PropertyDescriptorRepository {

    void create(PropertyDescriptor propertyDescriptor) throws IOException;
    PropertyDescriptor get(String name) throws IOException;
    List<PropertyDescriptor> getAll() throws IOException;

}

package task_manager.core.repository;

import task_manager.property_lib.PropertyDescriptor;

import java.io.IOException;
import java.util.List;

public interface PropertyDescriptorRepository {

    void create(PropertyDescriptor propertyDescriptor) throws IOException;
    PropertyDescriptor get(String name) throws IOException;
    List<PropertyDescriptor> find(String name) throws IOException;
    List<PropertyDescriptor> getAll() throws IOException;

}

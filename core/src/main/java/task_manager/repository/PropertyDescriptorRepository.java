package task_manager.repository;

import java.io.IOException;
import task_manager.data.property.PropertyDescriptor;
import task_manager.data.property.PropertyDescriptorCollection;

public interface PropertyDescriptorRepository {

    void create(PropertyDescriptor propertyDescriptor) throws IOException;
    PropertyDescriptor get(String name) throws IOException;
    PropertyDescriptorCollection getAll() throws IOException;

}

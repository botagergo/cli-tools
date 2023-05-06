package task_manager.db.property;

import java.io.IOException;

public interface PropertyDescriptorRepository {
    public PropertyDescriptor getPropertyDescriptor(String name) throws IOException;

    public PropertyDescriptorCollection getPropertyDescriptors() throws IOException;
}

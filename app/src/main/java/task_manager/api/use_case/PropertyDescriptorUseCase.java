package task_manager.api.use_case;

import java.io.IOException;
import com.google.inject.Inject;
import task_manager.db.property.PropertyDescriptor;
import task_manager.db.property.PropertyDescriptorCollection;
import task_manager.db.property.PropertyDescriptorRepository;

public class PropertyDescriptorUseCase {

    public PropertyDescriptor getPropertyDescriptor(String name) throws IOException {
        return propertyDescriptorRepository.getPropertyDescriptor(name);
    }

    public PropertyDescriptorCollection getPropertyDescriptors() throws IOException {
        return propertyDescriptorRepository.getPropertyDescriptors();
    }

    @Inject
    PropertyDescriptorRepository propertyDescriptorRepository;

}

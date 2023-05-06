package task_manager.logic.use_case;

import java.io.IOException;

import jakarta.inject.Inject;
import task_manager.repository.PropertyDescriptorRepository;
import task_manager.data.property.PropertyDescriptor;
import task_manager.data.property.PropertyDescriptorCollection;

public class PropertyDescriptorUseCase {

    @Inject
    public PropertyDescriptorUseCase(PropertyDescriptorRepository propertyDescriptorRepository) {
        this.propertyDescriptorRepository = propertyDescriptorRepository;
    }

    public void createPropertyDescriptor(PropertyDescriptor propertyDescriptor) throws IOException {
        propertyDescriptorRepository.create(propertyDescriptor);
    }

    public PropertyDescriptorCollection getPropertyDescriptors() throws IOException {
        return propertyDescriptorRepository.getAll();
    }

    final PropertyDescriptorRepository propertyDescriptorRepository;

}

package task_manager.logic.use_case;

import java.io.IOException;

import jakarta.inject.Inject;
import task_manager.repository.PropertyDescriptorRepository;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyDescriptorCollection;

public class PropertyDescriptorUseCase {

    @Inject
    public PropertyDescriptorUseCase(PropertyDescriptorRepository propertyDescriptorRepository) {
        this.propertyDescriptorRepository = propertyDescriptorRepository;
    }

    public void createPropertyDescriptor(PropertyDescriptor propertyDescriptor) throws IOException {
        propertyDescriptorRepository.create(propertyDescriptor);
    }

    public PropertyDescriptor getPropertyDescriptor(String name) throws IOException {
        return propertyDescriptorRepository.get(name);
    }

    public PropertyDescriptorCollection getPropertyDescriptors() throws IOException {
        return propertyDescriptorRepository.getAll();
    }

    final PropertyDescriptorRepository propertyDescriptorRepository;

}

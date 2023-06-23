package task_manager.logic.use_case.property_descriptor;

import jakarta.inject.Inject;
import task_manager.core.property.PropertyDescriptor;
import task_manager.core.property.PropertyException;
import task_manager.core.repository.PropertyDescriptorRepository;

import java.io.IOException;
import java.util.List;

public class PropertyDescriptorUseCaseImpl implements PropertyDescriptorUseCase {

    @Inject
    public PropertyDescriptorUseCaseImpl(PropertyDescriptorRepository propertyDescriptorRepository) {
        this.propertyDescriptorRepository = propertyDescriptorRepository;
    }

    @Override
    public void createPropertyDescriptor(PropertyDescriptor propertyDescriptor) throws IOException {
        propertyDescriptorRepository.create(propertyDescriptor);
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(String name) throws PropertyException, IOException {
        PropertyDescriptor propertyDescriptor = propertyDescriptorRepository.get(name);
        if (propertyDescriptor == null) {
            throw new PropertyException(PropertyException.Type.NotExist, name, null, null, null);
        } else {
            return propertyDescriptor;
        }
    }

    @Override
    public List<PropertyDescriptor> getPropertyDescriptors() throws IOException {
        return propertyDescriptorRepository.getAll();
    }

    final PropertyDescriptorRepository propertyDescriptorRepository;

}

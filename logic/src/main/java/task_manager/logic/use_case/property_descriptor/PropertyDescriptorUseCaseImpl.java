package task_manager.logic.use_case.property_descriptor;

import jakarta.inject.Inject;
import task_manager.core.repository.ConfigurationRepository;
import task_manager.core.repository.PropertyDescriptorRepository;
import task_manager.property_lib.PropertyDescriptor;
import task_manager.property_lib.PropertyException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PropertyDescriptorUseCaseImpl implements PropertyDescriptorUseCase {

    @Inject
    public PropertyDescriptorUseCaseImpl(
            PropertyDescriptorRepository propertyDescriptorRepository,
            ConfigurationRepository configurationRepository
    ) {
        this.propertyDescriptorRepository = propertyDescriptorRepository;
        this.configurationRepository = configurationRepository;
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
    public PropertyDescriptor findPropertyDescriptor(String name) throws PropertyException, IOException {
        if (!configurationRepository.allowPropertyPrefix()) {
            return getPropertyDescriptor(name);
        }

        List<PropertyDescriptor> propertyDescriptors = propertyDescriptorRepository.find(name);
        if (propertyDescriptors.isEmpty()) {
            throw new PropertyException(PropertyException.Type.NotExist, name, null, null, null);
        } else if (propertyDescriptors.size() > 1){
            Optional<PropertyDescriptor> propertyDescriptor = propertyDescriptors.stream().filter(pd -> pd.name().equals(name)).findAny();
            if (propertyDescriptor.isPresent()) {
                return propertyDescriptor.get();
            } else {
                throw new PropertyException(PropertyException.Type.MultipleMatches, name, null, null, null);
            }
        } else {
            return propertyDescriptors.get(0);
        }
    }

    @Override
    public List<PropertyDescriptor> getPropertyDescriptors() throws IOException {
        return propertyDescriptorRepository.getAll();
    }

    final PropertyDescriptorRepository propertyDescriptorRepository;
    private final ConfigurationRepository configurationRepository;


}

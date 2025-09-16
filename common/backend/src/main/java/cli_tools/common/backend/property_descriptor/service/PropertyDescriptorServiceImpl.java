package cli_tools.common.backend.property_descriptor.service;

import cli_tools.common.core.repository.ConfigurationRepository;
import cli_tools.common.core.repository.PropertyDescriptorRepository;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PropertyDescriptorServiceImpl implements PropertyDescriptorService {

    final PropertyDescriptorRepository propertyDescriptorRepository;
    private final ConfigurationRepository configurationRepository;

    @Inject
    public PropertyDescriptorServiceImpl(
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
    public PropertyDescriptor findPropertyDescriptor(String name) throws PropertyException, IOException {
        if (!configurationRepository.allowPropertyPrefix()) {
            return getPropertyDescriptor(name);
        }

        List<PropertyDescriptor> propertyDescriptors = propertyDescriptorRepository.find(name);
        if (propertyDescriptors.isEmpty()) {
            throw new PropertyException(PropertyException.Type.NotExist, name, null, null, null, null);
        } else if (propertyDescriptors.size() > 1) {
            Optional<PropertyDescriptor> propertyDescriptor = propertyDescriptors.stream().filter(pd -> pd.name().equals(name)).findAny();
            if (propertyDescriptor.isPresent()) {
                return propertyDescriptor.get();
            } else {
                throw new PropertyException(PropertyException.Type.MultipleMatches, name, null, null, null, null);
            }
        } else {
            return propertyDescriptors.get(0);
        }
    }

    @Override
    public List<PropertyDescriptor> getPropertyDescriptors() throws IOException {
        return propertyDescriptorRepository.getAll();
    }

    private PropertyDescriptor getPropertyDescriptor(String name) throws PropertyException, IOException {
        PropertyDescriptor propertyDescriptor = propertyDescriptorRepository.get(name);
        if (propertyDescriptor == null) {
            throw new PropertyException(PropertyException.Type.NotExist, name, null, null, null, null);
        } else {
            return propertyDescriptor;
        }
    }


}

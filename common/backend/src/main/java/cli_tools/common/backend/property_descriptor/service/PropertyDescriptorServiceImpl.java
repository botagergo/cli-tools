package cli_tools.common.backend.property_descriptor.service;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.repository.ConfigurationRepository;
import cli_tools.common.core.repository.PropertyDescriptorRepository;
import cli_tools.common.property_lib.PropertyDescriptor;
import jakarta.inject.Inject;

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
    public void createPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        propertyDescriptorRepository.create(propertyDescriptor);
    }

    @Override
    public PropertyDescriptor findPropertyDescriptor(String name) throws ServiceException {
        if (!configurationRepository.allowPropertyPrefix()) {
            return getPropertyDescriptor(name);
        }

        List<PropertyDescriptor> propertyDescriptors = propertyDescriptorRepository.find(name);
        if (propertyDescriptors.isEmpty()) {
            throw new ServiceException("Property does not exist: %s".formatted(name), null);
        } else if (propertyDescriptors.size() > 1) {
            Optional<PropertyDescriptor> propertyDescriptor = propertyDescriptors.stream().filter(pd -> pd.name().equals(name)).findAny();
            if (propertyDescriptor.isPresent()) {
                return propertyDescriptor.get();
            } else {
                throw new ServiceException("Multiple properties match: %s".formatted(name), null);
            }
        } else {
            return propertyDescriptors.getFirst();
        }
    }

    @Override
    public List<PropertyDescriptor> getPropertyDescriptors() {
        return propertyDescriptorRepository.getAll();
    }

    private PropertyDescriptor getPropertyDescriptor(String name) throws ServiceException {
        PropertyDescriptor propertyDescriptor = propertyDescriptorRepository.get(name);
        if (propertyDescriptor == null) {
            throw new ServiceException("Property does not exist: %s".formatted(name), null);
        } else {
            return propertyDescriptor;
        }
    }


}

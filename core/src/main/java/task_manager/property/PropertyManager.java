package task_manager.property;

import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import task_manager.repository.PropertyDescriptorRepository;

import java.io.IOException;

@Log4j2
public class PropertyManager {

    @Inject
    public PropertyManager(PropertyDescriptorRepository propertyDescriptorRepository) {
        this.propertyDescriptorRepository = propertyDescriptorRepository;
    }

    public Property getProperty(PropertyOwner propertyOwner, String propertyName)
            throws PropertyException, IOException {
        log.debug("getProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);

        Object propertyValue = getPropertyValue(propertyOwner, propertyDescriptor);
        return Property.fromRaw(propertyDescriptor, propertyValue);
    }

    public void setProperty(PropertyOwner propertyOwner, String propertyName, Object propertyValue)
            throws PropertyException, IOException {
        log.debug("setProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        Property property = Property.from(propertyDescriptor, propertyValue);
        propertyOwner.getRawProperties().put(propertyName, property.getRawValue());
    }

    public boolean hasRawProperty(PropertyOwner propertyOwner, String propertyName) {
        return propertyOwner.getRawProperties().containsKey(propertyName);
    }

    private PropertyDescriptor getPropertyDescriptor(String propertyName) throws PropertyException, IOException {
        PropertyDescriptor propertyDescriptor =
                propertyDescriptorRepository.get(propertyName);
        if (propertyDescriptor == null) {
            throw new PropertyException(PropertyException.Type.NotExist, propertyName, null, null,
                    null);
        }
        return propertyDescriptor;
    }

    private Object getPropertyValue(PropertyOwner propertyOwner,
            PropertyDescriptor propertyDescriptor) {
        Object propertyValue = propertyOwner.getRawProperties().get(propertyDescriptor.name());
        if (propertyValue == null) {
            log.debug("Property '{}' not set, getting default value", propertyDescriptor.name());
            propertyValue = propertyDescriptor.defaultValue();
        }
        return propertyValue;
    }

    private final PropertyDescriptorRepository propertyDescriptorRepository;

}

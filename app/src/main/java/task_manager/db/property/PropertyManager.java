package task_manager.db.property;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class PropertyManager {
    public PropertyManager(PropertyDescriptorCollection propertyDescriptors) {
        this.propertyDescriptors = propertyDescriptors;
    }

    public Property getProperty(PropertyOwner propertyOwner, String propertyName)
            throws PropertyException {
        log.debug("getProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);

        Object propertyValue = getPropertyValue(propertyOwner, propertyDescriptor);
        return Property.fromRaw(propertyDescriptor, propertyValue);
    }

    public void setProperty(PropertyOwner propertyOwner, String propertyName, Object propertyValue)
            throws PropertyException {
        log.debug("setProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        Property property = Property.from(propertyDescriptor, propertyValue);
        propertyOwner.getRawProperties().put(propertyName, property.getRawValue());
    }

    private PropertyDescriptor getPropertyDescriptor(String propertyName) throws PropertyException {
        PropertyDescriptor propertyDescriptor =
                propertyDescriptors.getPropertyDescriptor(propertyName);
        if (propertyDescriptor == null) {
            throw new PropertyException(PropertyException.Type.NotExist, propertyName, null, null,
                    null);
        }
        return propertyDescriptor;
    }

    private Object getPropertyValue(PropertyOwner propertyOwner,
            PropertyDescriptor propertyDescriptor) {
        Object propertyValue = propertyOwner.getRawProperties().get(propertyDescriptor.getName());
        if (propertyValue == null) {
            log.debug("Property '{}' not set, getting default value", propertyDescriptor.getName());
            propertyValue = propertyDescriptor.getDefaultValue();
        }
        return propertyValue;
    }

    private PropertyDescriptorCollection propertyDescriptors;

}

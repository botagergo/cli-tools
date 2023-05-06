package task_manager.data.property;

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

    public Object getPropertyUnchecked(PropertyOwner propertyOwner, String propertyName) {
        return propertyOwner.getRawProperties().getOrDefault(propertyName, null);
    }

    public void setProperty(PropertyOwner propertyOwner, String propertyName, Object propertyValue)
            throws PropertyException {
        log.debug("setProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        Property property = Property.from(propertyDescriptor, propertyValue);
        propertyOwner.getRawProperties().put(propertyName, property.getRawValue());
    }

    public void setPropertyUnchecked(PropertyOwner propertyOwner, String propertyName,
        Object propertyValue) {
        log.debug("setPropertyUnchecked - {}", propertyName);

        propertyOwner.getRawProperties().put(propertyName, propertyValue);
    }

    public boolean hasProperty(PropertyOwner propertyOwner, String propertyName) {
        return propertyOwner.getRawProperties().containsKey(propertyName);
    }

    public void validateUuidProperty(PropertyOwner propertyOwner, String propertyName)
        throws PropertyException {
        propertyOwner.getUuidProperty(propertyName);
    }

    private PropertyDescriptor getPropertyDescriptor(String propertyName) throws PropertyException {
        PropertyDescriptor propertyDescriptor =
                propertyDescriptors.get(propertyName);
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

    private final PropertyDescriptorCollection propertyDescriptors;

}

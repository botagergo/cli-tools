package task_manager.db.property;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PropertyManager {
    public PropertyManager(PropertyDescriptorCollection propertyDescriptors) {
        this.propertyDescriptors = propertyDescriptors;
    }

    public Object getProperty(PropertyOwner propertyOwner, String propertyName)
            throws PropertyException {
        log.debug("getProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);

        Object propertyValue = getPropertyValue(propertyOwner, propertyDescriptor);
        if (propertyValue == null) {
            return null;
        }

        if (propertyDescriptor.getIsList()) {
            return convertPropertyValues(propertyValue, propertyDescriptor, false);
        } else {
            return convertPropertyValueForRead(propertyValue, propertyDescriptor);
        }
    }

    public Boolean getBooleanProperty(PropertyOwner propertyOwner, String propertyName)
            throws PropertyException {
        log.debug("getBooleanProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        if (propertyDescriptor.getType() != PropertyDescriptor.Type.Boolean) {
            throw new PropertyException(PropertyException.Type.TypeMismatch, propertyName,
                    propertyDescriptor, null, PropertyDescriptor.Type.Boolean);
        }
        if (propertyDescriptor.getIsList()) {
            throw new PropertyException(PropertyException.Type.IsAList, propertyName,
                    propertyDescriptor, null, PropertyDescriptor.Type.Boolean);
        }

        Object propertyValue = getPropertyValue(propertyOwner, propertyDescriptor);
        if (propertyValue == null) {
            return null;
        }

        if (!(propertyValue instanceof Boolean)) {
            throw new PropertyException(PropertyException.Type.WrongValueType, propertyName,
                    propertyDescriptor, propertyValue, PropertyDescriptor.Type.Boolean);
        }

        return (boolean) propertyValue;
    }

    public String getStringProperty(PropertyOwner propertyOwner, String propertyName)
            throws PropertyException {
        log.debug("getStringProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        if (propertyDescriptor.getType() != PropertyDescriptor.Type.String) {
            throw new PropertyException(PropertyException.Type.TypeMismatch, propertyName,
                    propertyDescriptor, null, PropertyDescriptor.Type.String);
        }
        if (propertyDescriptor.getIsList()) {
            throw new PropertyException(PropertyException.Type.IsAList, propertyName,
                    propertyDescriptor, null, PropertyDescriptor.Type.String);
        }

        Object propertyValue = getPropertyValue(propertyOwner, propertyDescriptor);
        if (propertyValue == null) {
            return null;
        }

        if (!(propertyValue instanceof String)) {
            throw new PropertyException(PropertyException.Type.WrongValueType, propertyName,
                    propertyDescriptor, propertyValue, PropertyDescriptor.Type.Boolean);
        }

        return (String) propertyValue;
    }

    public UUID getUuidProperty(PropertyOwner propertyOwner, String propertyName)
            throws PropertyException {
        log.debug("getUuidProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        if (propertyDescriptor.getType() != PropertyDescriptor.Type.UUID) {
            throw new PropertyException(PropertyException.Type.TypeMismatch, propertyName,
                    propertyDescriptor, null, PropertyDescriptor.Type.UUID);
        }
        if (propertyDescriptor.getIsList()) {
            throw new PropertyException(PropertyException.Type.IsAList, propertyName,
                    propertyDescriptor, null, PropertyDescriptor.Type.UUID);
        }

        Object propertyValue = getPropertyValue(propertyOwner, propertyDescriptor);
        if (propertyValue == null) {
            return null;
        }

        if (!(propertyValue instanceof String)) {
            throw new PropertyException(PropertyException.Type.WrongValueType, propertyName,
                    propertyDescriptor, propertyValue, PropertyDescriptor.Type.UUID);
        }

        return UUID.fromString((String) propertyValue);
    }

    public List<UUID> getUuidListProperty(PropertyOwner propertyOwner, String propertyName)
            throws PropertyException {
        log.debug("getUuidListProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        if (propertyDescriptor.getType() != PropertyDescriptor.Type.UUID) {
            throw new PropertyException(PropertyException.Type.TypeMismatch, propertyName,
                    propertyDescriptor, null, PropertyDescriptor.Type.UUID);
        }
        if (!propertyDescriptor.getIsList()) {
            throw new PropertyException(PropertyException.Type.NotAList, propertyName,
                    propertyDescriptor, null, PropertyDescriptor.Type.UUID);
        }


        Object propertyValue = getPropertyValue(propertyOwner, propertyDescriptor);
        if (propertyValue == null) {
            return null;
        }

        List<?> propertyValues =
                convertPropertyValues((List<?>) propertyValue, propertyDescriptor, false);

        @SuppressWarnings("unchecked") // We already checked in convertPropertyValues that the items
                                       // are of correct type
        List<UUID> uuidValues = (List<UUID>) propertyValues;

        return uuidValues;
    }

    public void setProperty(PropertyOwner propertyOwner, String propertyName, Object propertyValue)
            throws PropertyException {
        log.debug("setProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);

        if (propertyDescriptor.getIsList()) {
            propertyOwner.getProperties().put(propertyName,
                    convertPropertyValues(propertyValue, propertyDescriptor, true));
        } else {
            propertyOwner.getProperties().put(propertyName,
                    convertPropertyValueForWrite(propertyValue, propertyDescriptor));
        }
    }

    public List<?> convertPropertyValues(Object propertyValues,
            PropertyDescriptor propertyDescriptor, boolean write) throws PropertyException {
        if (!(propertyValues instanceof List<?>)) {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                    propertyDescriptor.getName(), propertyDescriptor, propertyValues,
                    propertyDescriptor.getType());
        }

        List<Object> convertedPropertyValues = new ArrayList<>();
        for (Object propertyValue : (List<?>) propertyValues) {
            if (write) {
                convertedPropertyValues
                        .add(convertPropertyValueForWrite(propertyValue, propertyDescriptor));
            } else {
                convertedPropertyValues
                        .add(convertPropertyValueForRead(propertyValue, propertyDescriptor));
            }
        }
        return convertedPropertyValues;
    }

    private Object convertPropertyValueForRead(Object propertyValue,
            PropertyDescriptor propertyDescriptor) throws PropertyException {
        if ((propertyDescriptor.getType().equals(PropertyDescriptor.Type.String)
                && propertyValue instanceof String)
                || (propertyDescriptor.getType().equals(PropertyDescriptor.Type.Boolean)
                        && propertyValue instanceof Boolean)) {
            return propertyValue;
        } else if (propertyDescriptor.getType().equals(PropertyDescriptor.Type.UUID)
                && propertyValue instanceof String) {
            return UUID.fromString((String) propertyValue);
        } else {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                    propertyDescriptor.getName(), propertyDescriptor, propertyValue,
                    propertyDescriptor.getType());
        }
    }

    private Object convertPropertyValueForWrite(Object propertyValue,
            PropertyDescriptor propertyDescriptor) throws PropertyException {
        if ((propertyDescriptor.getType().equals(PropertyDescriptor.Type.String)
                && propertyValue instanceof String)
                || (propertyDescriptor.getType().equals(PropertyDescriptor.Type.Boolean)
                        && propertyValue instanceof Boolean)) {
            return propertyValue;
        } else if (propertyDescriptor.getType().equals(PropertyDescriptor.Type.UUID)
                && propertyValue instanceof UUID) {
            return ((UUID) propertyValue).toString();
        } else {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                    propertyDescriptor.getName(), propertyDescriptor, propertyValue,
                    propertyDescriptor.getType());
        }
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
        Object propertyValue = propertyOwner.getProperties().get(propertyDescriptor.getName());
        if (propertyValue == null) {
            log.debug("Property '{}' not set, getting default value", propertyDescriptor.getName());
            propertyValue = propertyDescriptor.getDefaultValue();
        }
        return propertyValue;
    }

    private PropertyDescriptorCollection propertyDescriptors;

}

package task_manager.data.property;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Property {

    private Property(PropertyDescriptor propertyDescriptor, Object value) {
        this.propertyDescriptor = propertyDescriptor;
        this.value = value;
    }

    public static Property fromRaw(PropertyDescriptor propertyDescriptor, Object propertyValue)
            throws PropertyException {
        if (propertyValue == null) {
            return new Property(propertyDescriptor, null);
        } else if (propertyDescriptor.isList()) {
            return new Property(propertyDescriptor,
                convertPropertyValues(propertyValue, propertyDescriptor, true));
        } else {
            return new Property(propertyDescriptor,
                getRawPropertyValueFromRaw(propertyValue, propertyDescriptor));
        }
    }

    public static Property from(PropertyDescriptor propertyDescriptor, Object propertyValue)
            throws PropertyException {
        if (propertyValue == null) {
            return new Property(propertyDescriptor, null);
        } else if (propertyDescriptor.isList()) {
            return new Property(propertyDescriptor,
                convertPropertyValues(propertyValue, propertyDescriptor, false));
        } else {
            return new Property(propertyDescriptor,
                getRawPropertyValue(propertyValue, propertyDescriptor));
        }
    }

    public Object getValue() throws PropertyException {
        if (propertyDescriptor.type() == PropertyDescriptor.Type.UUID) {
            if (propertyDescriptor.isList()) {
                return getUuidList();
            } else {
                return getUuid();
            }
        } else {
            return value;
        }
    }

    public Object getRawValue() {
        return value;
    }

    public Boolean getBoolean() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Boolean) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean);
        }

        if (value == null) {
            return null;
        }

        return (Boolean) value;
    }

    public String getString() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.String) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.String);
        }

        if (value == null) {
            return null;
        }

        return (String) value;
    }

    public UUID getUuid() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.UUID) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID);
        }

        if (value == null) {
            return null;
        }

        try {
            return UUID.fromString((String) value);
        } catch (IllegalArgumentException e) {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                propertyDescriptor.name(), propertyDescriptor, value,
                propertyDescriptor.type());
        }
    }

    public List<UUID> getUuidList() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.UUID) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID);
        } else if (!propertyDescriptor.isList()) {
            throw new PropertyException(PropertyException.Type.NotAList,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID);
        }

        if (value == null) {
            return null;
        }

        List<UUID> propertyValues = new ArrayList<>();
        for (Object uuidStr : (List<?>) value) {
            propertyValues.add(UUID.fromString((String) uuidStr));
        }
        return propertyValues;
    }

    public static List<?> convertPropertyValues(Object propertyValues,
        PropertyDescriptor propertyDescriptor, boolean fromRaw) throws PropertyException {
        if (!(propertyValues instanceof List<?>)) {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                propertyDescriptor.name(), propertyDescriptor, propertyValues,
                propertyDescriptor.type());
        }

        List<Object> convertedPropertyValues = new ArrayList<>();
        for (Object propertyValue : (List<?>) propertyValues) {
            if (propertyValue == null) {
                convertedPropertyValues.add(null);
            } else if (fromRaw) {
                convertedPropertyValues
                    .add(getRawPropertyValueFromRaw(propertyValue, propertyDescriptor));
            } else {
                convertedPropertyValues
                    .add(getRawPropertyValue(propertyValue, propertyDescriptor));
            }
        }
        return convertedPropertyValues;
    }

    private static Object getRawPropertyValueFromRaw(Object propertyValue,
            PropertyDescriptor propertyDescriptor) throws PropertyException {
        if ((propertyDescriptor.type().equals(PropertyDescriptor.Type.String)
                && propertyValue instanceof String)
                || (propertyDescriptor.type().equals(PropertyDescriptor.Type.Boolean)
                        && propertyValue instanceof Boolean)
                || (propertyDescriptor.type().equals(PropertyDescriptor.Type.UUID)
                        && propertyValue instanceof String)) {
            return propertyValue;
        } else {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                    propertyDescriptor.name(), propertyDescriptor, propertyValue,
                    propertyDescriptor.type());
        }
    }

    private static Object getRawPropertyValue(Object propertyValue,
        PropertyDescriptor propertyDescriptor) throws PropertyException {
        if ((propertyDescriptor.type().equals(PropertyDescriptor.Type.String)
            && propertyValue instanceof String)
            || (propertyDescriptor.type().equals(PropertyDescriptor.Type.Boolean)
                && propertyValue instanceof Boolean)) {
            return propertyValue;
        } else if (propertyDescriptor.type().equals(PropertyDescriptor.Type.UUID)
            && propertyValue instanceof UUID) {
            return propertyValue.toString();
        } else {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                propertyDescriptor.name(), propertyDescriptor, propertyValue,
                propertyDescriptor.type());
        }
    }

    private final PropertyDescriptor propertyDescriptor;
    private final Object value;

}

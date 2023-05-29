package task_manager.property;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Property {

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
            return rawValue;
        }
    }

    public Boolean getBoolean() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Boolean) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, rawValue,
                    PropertyDescriptor.Type.Boolean);
        }

        if (rawValue == null) {
            return null;
        }

        return (Boolean) rawValue;
    }

    public String getString() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.String) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, rawValue,
                    PropertyDescriptor.Type.String);
        }

        if (rawValue == null) {
            return null;
        }

        return (String) rawValue;
    }

    public UUID getUuid() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.UUID) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, rawValue,
                    PropertyDescriptor.Type.UUID);
        }

        if (rawValue == null) {
            return null;
        }

        try {
            return UUID.fromString((String) rawValue);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("This should not happen");
        }
    }

    public List<UUID> getUuidList() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.UUID) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, rawValue,
                    PropertyDescriptor.Type.UUID);
        } else if (!propertyDescriptor.isList()) {
            throw new PropertyException(PropertyException.Type.NotAList,
                    propertyDescriptor.name(), propertyDescriptor, rawValue,
                    PropertyDescriptor.Type.UUID);
        }

        if (rawValue == null) {
            return null;
        }

        List<UUID> propertyValues = new ArrayList<>();
        for (Object uuidStr : (List<?>) rawValue) {
            propertyValues.add(UUID.fromString((String) uuidStr));
        }
        return propertyValues;
    }

    private static List<?> convertPropertyValues(Object propertyValues,
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

    @Getter private final PropertyDescriptor propertyDescriptor;
    @Getter private final Object rawValue;

}

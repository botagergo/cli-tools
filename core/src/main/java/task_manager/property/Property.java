package task_manager.property;

import lombok.*;

import java.util.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Property {

    public static Property from(PropertyDescriptor propertyDescriptor, Object propertyValue)
            throws PropertyException {
        if (propertyValue != null) {
            if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
                checkPropertyValueList(propertyDescriptor, propertyValue);
            } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
                checkPropertyValueSet(propertyDescriptor, propertyValue);
            } else {
                checkPropertyValue(propertyDescriptor, propertyValue);
            }
        }
        return new Property(propertyDescriptor, propertyValue);
    }

    public static Property fromUnchecked(PropertyDescriptor propertyDescriptor, Object propertyValue) {
        return new Property(propertyDescriptor, propertyValue);
    }

    public Boolean getBoolean() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Boolean) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean);
        }
        return getBooleanUnchecked();
    }

    public Boolean getBooleanUnchecked() {
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

        return getStringUnchecked();
    }

    public String getStringUnchecked() {
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

        return (UUID) value;
    }

    public List<UUID> getUuidList() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.UUID) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID);
        }

        if (value == null) {
            return null;
        }

        List<UUID> valueList = new ArrayList<>();
        for (Object value : (List<?>) value) {
            valueList.add((UUID) value);
        }
        return valueList;
    }

    public List<String> getStringList() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.String) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.String);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.String);
        }

        if (value == null) {
            return null;
        }

        List<String> valueList = new ArrayList<>();
        for (Object value : (List<?>) value) {
            valueList.add((String) value);
        }
        return valueList;
    }

    public List<Boolean> getBooleanList() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Boolean) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean);
        }

        if (value == null) {
            return null;
        }

        List<Boolean> valueList = new ArrayList<>();
        for (Object value : (List<?>) value) {
            valueList.add((Boolean) value);
        }
        return valueList;
    }

    public LinkedHashSet<UUID> getUuidSet() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.UUID) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID);
        }

        if (value == null) {
            return null;
        }

        LinkedHashSet<UUID> valueSet = new LinkedHashSet<>();
        for (Object value : (LinkedHashSet<?>) value) {
            valueSet.add((UUID) value);
        }
        return valueSet;
    }

    public LinkedHashSet<String> getStringSet() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.String) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.String);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.String);
        }

        if (value == null) {
            return null;
        }

        LinkedHashSet<String> valueSet = new LinkedHashSet<>();
        for (Object value : (LinkedHashSet<?>) value) {
            valueSet.add((String) value);
        }
        return valueSet;
    }

    public LinkedHashSet<Boolean> getBooleanSet() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Boolean) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean);
        }

        if (value == null) {
            return null;
        }

        LinkedHashSet<Boolean> valueSet = new LinkedHashSet<>();
        for (Object value : (LinkedHashSet<?>) value) {
            valueSet.add((Boolean) value);
        }
        return valueSet;
    }

    public List<Object> getList() throws PropertyException {
        if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID);
        }

        if (value == null) {
            return null;
        }

        return new ArrayList<>((List<?>) value);
    }

    public LinkedHashSet<Object> getSet() throws PropertyException {
        if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID);
        }

        if (value == null) {
            return null;
        }
        return new LinkedHashSet<>((LinkedHashSet<?>) value);
    }

    private static void checkPropertyValue(PropertyDescriptor propertyDescriptor, Object propertyValue) throws PropertyException {
        if ( propertyDescriptor.type().equals(PropertyDescriptor.Type.String) &&
                    !(propertyValue instanceof String) ||
                propertyDescriptor.type().equals(PropertyDescriptor.Type.Boolean) &&
                    !(propertyValue instanceof Boolean) ||
                propertyDescriptor.type().equals(PropertyDescriptor.Type.UUID) &&
                    !(propertyValue instanceof UUID)) {

            throw new PropertyException(PropertyException.Type.WrongValueType,
                    propertyDescriptor.name(), propertyDescriptor, propertyValue,
                    propertyDescriptor.type());
        }
    }

    private static void checkPropertyValueList(PropertyDescriptor propertyDescriptor, Object propertyValues) throws PropertyException {
        if (!(propertyValues instanceof List<?>)) {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                propertyDescriptor.name(), propertyDescriptor, propertyValues,
                propertyDescriptor.type());
        }

        for (Object propertyValue : (List<?>) propertyValues) {
            if (propertyValue != null) {
                checkPropertyValue(propertyDescriptor, propertyValue);
            }
        }
    }

    private static void checkPropertyValueSet(PropertyDescriptor propertyDescriptor, Object propertyValues) throws PropertyException {
        if (!(propertyValues instanceof Set<?>)) {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                    propertyDescriptor.name(), propertyDescriptor, propertyValues,
                    propertyDescriptor.type());
        }

        for (Object propertyValue : (Set<?>) propertyValues) {
            if (propertyValue != null) {
                checkPropertyValue(propertyDescriptor, propertyValue);
            }
        }
    }

    @Getter private final PropertyDescriptor propertyDescriptor;
    @Getter private final Object value;

}

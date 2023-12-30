package task_manager.property_lib;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Property {

    public static Property from(PropertyDescriptor propertyDescriptor, Object propertyValue)
            throws PropertyException {
        if (propertyValue != null) {
            if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
                propertyValue = convertPropertyValueList(propertyDescriptor, propertyValue);
            } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
                propertyValue = convertPropertyValueSet(propertyDescriptor, propertyValue);
            } else {
                checkPropertyValue(propertyDescriptor, propertyValue);
            }
        }
        return fromUnchecked(propertyDescriptor, propertyValue);
    }

    public static Property fromUnchecked(PropertyDescriptor propertyDescriptor, Object propertyValue) {
        return new Property(propertyDescriptor, propertyValue);
    }

    public Boolean getBoolean() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Boolean) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean, null);
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
                    PropertyDescriptor.Type.String, null);
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
                    PropertyDescriptor.Type.UUID, null);
        }

        if (value == null) {
            return null;
        }

        return (UUID) value;
    }

    private static void checkPropertyValue(PropertyDescriptor propertyDescriptor, Object propertyValue) throws PropertyException {
        if (propertyDescriptor.type().equals(PropertyDescriptor.Type.String) &&
                !(propertyValue instanceof String) ||
                propertyDescriptor.type().equals(PropertyDescriptor.Type.Boolean) &&
                        !(propertyValue instanceof Boolean) ||
                propertyDescriptor.type().equals(PropertyDescriptor.Type.UUID) &&
                        !(propertyValue instanceof UUID) ||
                propertyDescriptor.type().equals(PropertyDescriptor.Type.Integer) &&
                        !(propertyValue instanceof Integer)) {

            throw new PropertyException(PropertyException.Type.WrongValueType,
                    propertyDescriptor.name(), propertyDescriptor, propertyValue,
                    propertyDescriptor.type(), null);
        }
    }

    public Integer getInteger() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Integer) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Integer, null);
        }

        return getIntegerUnchecked();
    }

    public Integer getIntegerUnchecked() {
        if (value == null) {
            return null;
        }

        return (Integer) value;
    }

    public LocalDate getDate() throws PropertyException {
        String propertyValue = getString();

        if (!(propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.DateSubtype)) {
            throw new PropertyException(PropertyException.Type.SubtypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, propertyValue,
                    PropertyDescriptor.Type.String, "Date");
        }

        if (propertyValue == null) {
            return null;
        }

        try {
            return LocalDate.parse(propertyValue, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                    propertyDescriptor.name(), propertyDescriptor, propertyValue,
                    propertyDescriptor.type(), "Date");
        }
    }

    public LocalTime getTime() throws PropertyException {
        String propertyValue = getString();

        if (!(propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.TimeSubtype)) {
            throw new PropertyException(PropertyException.Type.SubtypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, propertyValue,
                    PropertyDescriptor.Type.String, "Time");
        }

        if (propertyValue == null) {
            return null;
        }

        try {
            return LocalTime.parse(propertyValue, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException e) {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                    propertyDescriptor.name(), propertyDescriptor, propertyValue,
                    propertyDescriptor.type(), "Date");
        }
    }

    @SuppressWarnings("unchecked")
    public List<UUID> getUuidList() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.UUID) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID, null);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID, null);
        }

        if (value == null) {
            return null;
        }

        return (List<UUID>) value;
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.String) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.String, null);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.String, null);
        }

        if (value == null) {
            return null;
        }

        return (List<String>) value;
    }

    @SuppressWarnings("unchecked")
    public List<Boolean> getBooleanList() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Boolean) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean, null);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean, null);
        }

        if (value == null) {
            return null;
        }

        return (List<Boolean>) value;
    }

    @SuppressWarnings("unchecked")
    public Set<UUID> getUuidSet() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.UUID) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID, null);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.UUID, null);
        }

        if (value == null) {
            return null;
        }

        return (Set<UUID>) value;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getStringSet() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.String) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.String, null);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.String, null);
        }

        if (value == null) {
            return null;
        }

        return (Set<String>) value;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getList() throws PropertyException {
        if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value, null, null);
        }

        if (value == null) {
            return null;
        }

        return (List<Object>) value;
    }

    @SuppressWarnings("unchecked")
    public Set<Object> getSet() throws PropertyException {
        if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value, null, null);
        }

        if (value == null) {
            return null;
        }
        return (Set<Object>) value;
    }


    @SuppressWarnings("unchecked")
    public Collection<Object> getCollection() throws PropertyException {
        if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET
        && propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value, null, null);
        }

        if (value == null) {
            return null;
        }

        return (Collection<Object>) value;
    }

    @SuppressWarnings("unchecked")
    public Set<Boolean> getBooleanSet() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Boolean) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean, null);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Boolean, null);
        }

        if (value == null) {
            return null;
        }

        return (Set<Boolean>) value;
    }

    private static List<Object> convertPropertyValueList(PropertyDescriptor propertyDescriptor, Object propertyValues) throws PropertyException {
        if (!(propertyValues instanceof List<?> propertyValuesList)) {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                propertyDescriptor.name(), propertyDescriptor, propertyValues,
                propertyDescriptor.type(), null);
        }

        for (Object propertyValue : propertyValuesList) {
            if (propertyValue != null) {
                checkPropertyValue(propertyDescriptor, propertyValue);
            }
        }

        return Collections.unmodifiableList(propertyValuesList);
    }

    private static Set<Object> convertPropertyValueSet(PropertyDescriptor propertyDescriptor, Object propertyValues) throws PropertyException {
        if (!(propertyValues instanceof Set<?> propertyValuesSet)) {
            throw new PropertyException(PropertyException.Type.WrongValueType,
                    propertyDescriptor.name(), propertyDescriptor, propertyValues,
                    propertyDescriptor.type(), null);
        }

        for (Object propertyValue : propertyValuesSet) {
            if (propertyValue != null) {
                checkPropertyValue(propertyDescriptor, propertyValue);
            }
        }

        return Collections.unmodifiableSet(propertyValuesSet);
    }

    private final PropertyDescriptor propertyDescriptor;
    private final Object value;

}

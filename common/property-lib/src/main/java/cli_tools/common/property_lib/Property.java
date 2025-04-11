package cli_tools.common.property_lib;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Property {

    @NonNull
    private final PropertyDescriptor propertyDescriptor;
    private final Object value;

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

        return getUuidUnchecked();
    }

    public UUID getUuidUnchecked() {
        if (value == null) {
            return null;
        }
        return (UUID) value;
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

        return getUuidListUnchecked();
    }

    @SuppressWarnings("unchecked")
    public List<UUID> getUuidListUnchecked() {
        if (value == null) {
            return null;
        }
        return (List<UUID>) value;
    }

    public List<Integer> getIntegerList() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Integer) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Integer, null);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Integer, null);
        }

        return getIntegerListUnchecked();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getIntegerListUnchecked() {
        if (value == null) {
            return null;
        }
        return (List<Integer>) value;
    }

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

        return getStringListUnchecked();
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringListUnchecked() {
        if (value == null) {
            return null;
        }
        return (List<String>) value;
    }

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

        return getBooleanListUnchecked();
    }

    @SuppressWarnings("unchecked")
    public List<Boolean> getBooleanListUnchecked() {
        if (value == null) {
            return null;
        }
        return (List<Boolean>) value;
    }

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

        return getUuidSetUnchecked();
    }

    @SuppressWarnings("unchecked")
    public Set<UUID> getUuidSetUnchecked() {
        if (value == null) {
            return null;
        }
        return (Set<UUID>) value;
    }

    public Set<Integer> getIntegerSet() throws PropertyException {
        if (propertyDescriptor.type() != PropertyDescriptor.Type.Integer) {
            throw new PropertyException(PropertyException.Type.TypeMismatch,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Integer, null);
        } else if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value,
                    PropertyDescriptor.Type.Integer, null);
        }

        return getIntegerSetUnchecked();
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> getIntegerSetUnchecked() {
        if (value == null) {
            return null;
        }
        return (Set<Integer>) value;
    }

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

        return getStringSetUnchecked();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getStringSetUnchecked() {
        if (value == null) {
            return null;
        }
        return (Set<String>) value;
    }

    public List<Object> getList() throws PropertyException {
        if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value, null, null);
        }

        return getListUnchecked();
    }

    @SuppressWarnings("unchecked")
    public List<Object> getListUnchecked() {
        if (value == null) {
            return null;
        }
        return (List<Object>) value;
    }

    public Set<Object> getSet() throws PropertyException {
        if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value, null, null);
        }

        return getSetUnchecked();
    }

    @SuppressWarnings("unchecked")
    public Set<Object> getSetUnchecked() throws PropertyException {
        if (value == null) {
            return null;
        }
        return (Set<Object>) value;
    }

    public Collection<Object> getCollection() throws PropertyException {
        if (propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SET
                && propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.LIST) {
            throw new PropertyException(PropertyException.Type.WrongMultiplicity,
                    propertyDescriptor.name(), propertyDescriptor, value, null, null);
        }

        return getCollectionUnchecked();
    }

    @SuppressWarnings("unchecked")
    public Collection<Object> getCollectionUnchecked() {
        if (value == null) {
            return null;
        }
        return (Collection<Object>) value;
    }

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

        return getBooleanSetUnchecked();
    }

    @SuppressWarnings("unchecked")
    public Set<Boolean> getBooleanSetUnchecked() {
        if (value == null) {
            return null;
        }
        return (Set<Boolean>) value;
    }

}

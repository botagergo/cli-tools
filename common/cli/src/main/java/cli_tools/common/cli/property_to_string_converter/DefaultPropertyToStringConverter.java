package cli_tools.common.cli.property_to_string_converter;

import cli_tools.common.backend.property_converter.PropertyConverterException;
import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.cli.DateTimeFormatter;
import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.backend.label.service.LabelService;
import cli_tools.common.backend.ordered_label.service.OrderedLabelService;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Log4j2
public class DefaultPropertyToStringConverter implements PropertyToStringConverter {
    private String listSeparator = ", ";
    private String nullString = "";
    private String nullStringInList = "<null>";
    private LabelService labelService;
    private OrderedLabelService orderedLabelService;
    private DateTimeFormatter dateTimeFormatter;

    @Inject
    public DefaultPropertyToStringConverter(
            LabelService labelService,
            OrderedLabelService orderedLabelService,
            DateTimeFormatter dateTimeFormatter) {
        this.labelService = labelService;
        this.orderedLabelService = orderedLabelService;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public String propertyToString(String propertyName, Property property) throws PropertyConverterException {
        if (property.getValue() == null) {
            return nullString;
        }

        try {
            switch (property.getPropertyDescriptor().type()) {
                case String -> {
                    return stringToString(property);
                }
                case UUID -> {
                    return uuidToString(property);
                }
                case Boolean -> {
                    return booleanToString(property);
                }
                case Integer -> {
                    return integerToString(property);
                }
                case Date -> {
                    return dateToString(property);
                }
                case Time -> {
                    return timeToString(property);
                }
                default -> throw new RuntimeException();
            }
        } catch (ServiceException e) {
            throw new PropertyConverterException("Error converting property '%s' to string: %s".formatted(propertyName, e.getMessage()), e);
        }
    }

    private String stringToString(Property property) {
        PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE) {
            return property.getStringUnchecked();
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            return stringStreamToString(property.getStringListUnchecked().stream(), false);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            return stringStreamToString(property.getStringSetUnchecked().stream(), true);
        } else {
            throw new RuntimeException();
        }
    }

    private String stringStreamToString(Stream<String> stringStream, boolean sort) {
        var stream = stringStream.map(string -> Objects.requireNonNullElse(string, nullStringInList));
        if (sort) {
            stream = stream.sorted();
        }
        return stream.collect(Collectors.joining(listSeparator));
    }

    private String uuidToString(Property property) throws ServiceException {
        PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        if (propertyDescriptor.subtype() != null) {
            if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.LabelSubtype(String labelType)) {
                return labelUuidToString(labelType, property);
            } else {
                throw new RuntimeException();
            }
        }
        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE) {
            return property.getUuidUnchecked().toString();
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            return uuidStreamToString(property.getUuidListUnchecked().stream(), false);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            return uuidStreamToString(property.getUuidSetUnchecked().stream(), true);
        } else {
            throw new RuntimeException();
        }
    }

    private String uuidStreamToString(Stream<UUID> uuids, boolean sort) {
        List<String> str = new ArrayList<>();
        for (UUID uuid : uuids.toArray(UUID[]::new)) {
            if (uuid == null) {
                str.add(nullStringInList);
            } else {
                str.add(uuid.toString());
            }
        }
        var stream = str.stream();
        if (sort) {
            stream = stream.sorted();
        }
        return stream.collect(Collectors.joining(listSeparator));
    }

    private String labelStringStreamToString(Collection<UUID> labelUuids, String labelType, boolean sort) throws ServiceException {
        List<String> labelTexts = new ArrayList<>();
        for (UUID labelUuid : labelUuids) {
            labelTexts.add(labelUuidToString_(labelType, labelUuid));
        }
        var stream = labelTexts.stream();
        if (sort) {
            stream = stream.sorted();
        }
        return stream.collect(Collectors.joining(listSeparator));
    }

    private String labelUuidToString(String labelType, Property property) throws ServiceException {
        if (property.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.SINGLE) {
            return labelUuidToString_(labelType, property.getUuidUnchecked());
        } else if (property.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            return labelStringStreamToString(property.getUuidListUnchecked(), labelType, false);
        } else if (property.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            return labelStringStreamToString(property.getUuidSetUnchecked(), labelType, true);
        } else {
            throw new RuntimeException();
        }
    }

    private String labelUuidToString_(String labelType, UUID uuid) throws ServiceException {
        String labelText = labelService.getLabel(labelType, uuid);
        if (labelText != null) {
            return labelText;
        } else {
            log.warn("Label with UUID '{}' does not exist", uuid.toString());
            return "<NO_SUCH_LABEL:" + uuid + ">";
        }
    }

    private String integerToString(Property property) throws ServiceException {
        PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE) {
            return integerToString(property.getIntegerUnchecked(), propertyDescriptor);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            return integerStreamToString(property.getIntegerListUnchecked().stream(), propertyDescriptor, false);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            return integerStreamToString(property.getIntegerListUnchecked().stream(), propertyDescriptor, true);
        } else {
            throw new RuntimeException();
        }
    }

    private String integerToString(Integer integer, PropertyDescriptor propertyDescriptor) throws ServiceException {
        if (propertyDescriptor.subtype() != null) {
            if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.OrderedLabelSubtype(
                    String orderedLabelType
            )) {
                return orderedLabelIntegerToString(integer, orderedLabelType);
            } else {
                throw new RuntimeException();
            }
        } else {
            return integer.toString();
        }
    }

    private String integerStreamToString(Stream<Integer> integers, PropertyDescriptor propertyDescriptor, boolean sort) throws ServiceException {
        if (propertyDescriptor.subtype() != null) {
            if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.OrderedLabelSubtype(
                    String orderedLabelType
            )) {
                return orderedLabelIntegerStreamToString(integers, orderedLabelType, sort);
            } else {
                throw new RuntimeException();
            }
        } else {
            return integerStreamToString(integers, sort);
        }
    }

    private String integerStreamToString(Stream<Integer> integers, boolean sort) {
        var stream = integers.map(integer -> integer == null ? nullStringInList : integer.toString());
        if (sort) {
            stream = stream.sorted();
        }
        return stream.collect(Collectors.joining(listSeparator));
    }

    private String orderedLabelIntegerStreamToString(Stream<Integer> integers, String orderedLabelType, boolean sort) throws ServiceException {
        List<String> str = new ArrayList<>();
        for (Integer integer : integers.toArray(Integer[]::new)) {
            if (integer == null) {
                str.add(nullStringInList);
            } else {
                str.add(orderedLabelIntegerToString(integer, orderedLabelType));
            }
        }
        var stream = str.stream();
        if (sort) {
            stream = stream.sorted();
        }
        return stream.collect(Collectors.joining(listSeparator));
    }

    private String orderedLabelIntegerToString(Integer integer, String labelType) throws ServiceException {
        OrderedLabel orderedLabel = orderedLabelService.getOrderedLabel(labelType, integer);
        if (orderedLabel == null) {
            log.warn("Label with ID '" + integer + "' does not exist");
            return "<" + integer + ">";
        }
        return orderedLabel.text();
    }

    private String booleanToString(Property property) {
        PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE) {
            return booleanToString(property.getBooleanUnchecked(), nullString);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            return property.getBooleanListUnchecked().stream()
                    .map(bool -> booleanToString(bool, nullStringInList)).collect(Collectors.joining(listSeparator));
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            return property.getBooleanSetUnchecked().stream()
                    .map(bool -> booleanToString(bool, nullStringInList)).sorted().collect(Collectors.joining(listSeparator));
        } else {
            throw new RuntimeException();
        }
    }

    private String booleanToString(Boolean bool, String nullString) {
        return bool == null ? nullString : bool ? "yes" : "no";
    }

    private String dateToString(Property property) {
        PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE) {
            return dateToString_(property);
        } else {
            throw new NotImplementedException("Date list/set is not implemented");
        }
    }

    private String dateToString_(Property property) {
        LocalDate localDate = property.getDateUnchecked();
        return localDate != null ? dateTimeFormatter.formatLocalDate(localDate) : nullString;
    }

    private String timeToString(Property property) {
        PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE) {
            return timeToString_(property);
        } else {
            throw new NotImplementedException("Date list/set is not implemented");
        }
    }

    private String timeToString_(Property property) {
        LocalTime localTime = property.getTimeUnchecked();
        return localTime != null ? dateTimeFormatter.formatLocalTime(localTime) : nullString;
    }
}

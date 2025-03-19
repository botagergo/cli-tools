package cli_tools.common.cli.property_to_string_converter;

import cli_tools.common.cli.DateTimeFormatter;
import cli_tools.common.label.service.LabelService;
import cli_tools.common.ordered_label.service.OrderedLabelService;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;
import cli_tools.common.core.data.Label;
import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.core.repository.LabelRepositoryFactory;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Log4j2
public class DefaultPropertyToStringConverter implements PropertyToStringConverter {
    @Inject
    public DefaultPropertyToStringConverter(
            LabelService labelService,
            OrderedLabelService orderedLabelService,
            DateTimeFormatter dateTimeFormatter) {
        this.labelService = labelService;
        this.orderedLabelService = orderedLabelService;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public String propertyToString(String propertyName, Property property) throws IOException {
        if (property.getValue() == null) {
            return nullString;
        }

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
            default -> throw new RuntimeException();
        }
    }

    private String stringToString(Property property) {
        PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        if (propertyDescriptor.subtype() != null) {
            if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.DateSubtype) {
                return dateToString(property);
            } else if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.TimeSubtype) {
                return timeToString(property);
            } else {
                throw new RuntimeException();
            }
        }
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

    private String uuidToString(Property property) throws IOException {
        PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE) {
            return uuidToString(property.getUuidUnchecked(), propertyDescriptor);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            return uuidStreamToString(property.getUuidListUnchecked().stream(), propertyDescriptor, false);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            return uuidStreamToString(property.getUuidSetUnchecked().stream(), propertyDescriptor, true);
        } else {
            throw new RuntimeException();
        }
    }

    private String uuidToString(UUID uuid, PropertyDescriptor propertyDescriptor) throws IOException {
        if (propertyDescriptor.subtype() != null) {
            if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.LabelSubtype labelSubtype) {
                return labelUuidToString(uuid, labelSubtype.labelType());
            } else {
                throw new RuntimeException();
            }
        } else {
            return uuid.toString();
        }
    }

    private String uuidStreamToString(Stream<UUID> uuids, PropertyDescriptor propertyDescriptor, boolean sort) throws IOException {
        if (propertyDescriptor.subtype() != null) {
            if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.LabelSubtype labelSubtype) {
                return labelUuidStreamToString(uuids, labelSubtype.labelType(), sort);
            } else {
                throw new RuntimeException();
            }
        } else {
            return uuidStreamToString(uuids, sort);
        }
    }

    private String labelUuidStreamToString(Stream<UUID> uuids, String labelType, boolean sort) throws IOException {
        List<String> str = new ArrayList<>();
        for (UUID uuid : uuids.toArray(UUID[]::new)) {
            if (uuid == null) {
                str.add(nullStringInList);
            } else {
                str.add(labelUuidToString(uuid, labelType));
            }
        }
        var stream = str.stream();
        if (sort) {
            stream = stream.sorted();
        }
        return stream.collect(Collectors.joining(listSeparator));
    }

    private String uuidStreamToString(Stream<UUID> uuids, boolean sort) throws IOException {
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

    private String labelUuidToString(UUID uuid, String labelType) throws IOException {
        Label label = labelService.getLabel(labelType, uuid);
        if (label == null) {
            log.warn("Label with UUID '" + uuid + "' does not exist");
            return "<" + uuid.toString() + ">";
        }
        return label.text();
    }

    private String integerToString(Property property) throws IOException {
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

    private String integerToString(Integer integer, PropertyDescriptor propertyDescriptor) throws IOException {
        if (propertyDescriptor.subtype() != null) {
            if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.OrderedLabelSubtype orderedLabelSubtype) {
                return orderedLabelIntegerToString(integer, orderedLabelSubtype.orderedLabelType());
            } else {
                throw new RuntimeException();
            }
        } else {
            return integer.toString();
        }
    }

    private String integerStreamToString(Stream<Integer> integers, PropertyDescriptor propertyDescriptor, boolean sort) throws IOException {
        if (propertyDescriptor.subtype() != null) {
            if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.OrderedLabelSubtype orderedLabelSubtype) {
                return orderedLabelIntegerStreamToString(integers, orderedLabelSubtype.orderedLabelType(), sort);
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

    private String orderedLabelIntegerStreamToString(Stream<Integer> integers, String orderedLabelType, boolean sort) throws IOException {
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

    private String orderedLabelIntegerToString(Integer integer, String labelType) throws IOException {
        OrderedLabel orderedLabel = orderedLabelService.getOrderedLabel(labelType, integer);
        if (orderedLabel == null) {
            log.warn("Label with UUID '" + integer + "' does not exist");
            return "<" + integer.toString() + ">";
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
        try {
            LocalDate localDate = property.getDate();
            return localDate != null ? dateTimeFormatter.formatLocalDate(localDate) : nullString;
        } catch (PropertyException e) {
            return "<INVALID_DATE:" + property.getStringUnchecked() + ">";
        }
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
        try {
            LocalTime localTime = property.getTime();
            return localTime != null ? dateTimeFormatter.formatLocalTime(localTime) : nullString;
        } catch (PropertyException e) {
            return "<INVALID_TIME:" + property.getStringUnchecked() + ">";
        }
    }

    private String listSeparator = ", ";
    private String nullString = "";
    private String nullStringInList = "<null>";
    private LabelRepositoryFactory labelRepositoryFactory;
    private LabelService labelService;
    private OrderedLabelService orderedLabelService;
    private DateTimeFormatter dateTimeFormatter;
}

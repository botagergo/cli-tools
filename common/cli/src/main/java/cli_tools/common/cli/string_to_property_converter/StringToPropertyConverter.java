package cli_tools.common.cli.string_to_property_converter;

import cli_tools.common.cli.DateTimeParser;
import cli_tools.common.cli.argument.PropertyArgument;
import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.core.data.Predicate;
import cli_tools.common.core.data.property.Affinity;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.core.data.property.ModifyPropertySpec;
import cli_tools.common.label.service.LabelService;
import cli_tools.common.ordered_label.service.OrderedLabelService;
import cli_tools.common.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.temp_id_mapping.TempIDManager;
import cli_tools.common.util.Utils;
import jakarta.inject.Inject;
import lombok.NonNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class StringToPropertyConverter {

    private final PropertyDescriptorService propertyDescriptorService;
    private final LabelService labelService;
    private final OrderedLabelService orderedLabelService;
    private final TempIDManager tempIdManager;
    private final DateTimeParser dateTimeParser;

    @Inject
    public StringToPropertyConverter(
            LabelService labelService,
            OrderedLabelService orderedLabelService,
            PropertyDescriptorService propertyDescriptorService,
            TempIDManager tempIdManager
    ) {
        this.labelService = labelService;
        this.orderedLabelService = orderedLabelService;
        this.propertyDescriptorService = propertyDescriptorService;
        this.tempIdManager = tempIdManager;
        this.dateTimeParser = new DateTimeParser();
    }

    private static PropertyDescriptor.Subtype.OrderedLabelSubtype getOrderedLabelSubtype(PropertyDescriptor propertyDescriptor) throws StringToPropertyConverterException {
        PropertyDescriptor.Subtype.IntegerSubtype integerSubtype = propertyDescriptor.getIntegerSubtypeUnchecked();
        if (!(integerSubtype instanceof PropertyDescriptor.Subtype.OrderedLabelSubtype)) {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.NoAssociatedLabel,
                    "property '" + propertyDescriptor.name() + "' is not a label", propertyDescriptor.name());
        }

        return (PropertyDescriptor.Subtype.OrderedLabelSubtype) integerSubtype;
    }

    public List<FilterPropertySpec> convertPropertiesForFiltering(
            @NonNull List<PropertyArgument> propertyArguments,
            boolean createUuidIfNotExists
    ) throws IOException, StringToPropertyConverterException, PropertyException {
        List<FilterPropertySpec> filterPropertySpecs = new ArrayList<>();

        for (PropertyArgument propertyArgument : propertyArguments) {
            String propertyName = propertyArgument.propertyName();
            List<String> propertyValue = propertyArgument.values();

            Predicate predicate = parsePredicate(propertyArgument.predicate());

            if (propertyValue == null && !(predicate == Predicate.NULL || predicate == Predicate.EMPTY)) {
                throw new StringToPropertyConverterException(
                        StringToPropertyConverterException.Type.MissingPropertyValue,
                        "missing value for property '" + propertyName + "'",
                        propertyName);
            } else if (propertyValue != null && (predicate == Predicate.NULL || predicate == Predicate.EMPTY)) {
                throw new StringToPropertyConverterException(
                        StringToPropertyConverterException.Type.UnexpectedPropertyValue,
                        "unexpected property value for predicate '" + predicate + "'",
                        propertyName);
            }

            PropertyDescriptor propertyDescriptor = propertyDescriptorService.findPropertyDescriptor(propertyName);

            if (propertyValue == null) {
                filterPropertySpecs.add(new FilterPropertySpec(propertyDescriptor, null, propertyArgument.affinity() == Affinity.NEGATIVE, predicate));
            } else {
                List<Object> convertedValue = stringListToProperty(propertyDescriptor, propertyValue, createUuidIfNotExists);
                filterPropertySpecs.add(new FilterPropertySpec(propertyDescriptor, convertedValue, propertyArgument.affinity() == Affinity.NEGATIVE, parsePredicate(propertyArgument.predicate())));
            }
        }

        return filterPropertySpecs;
    }

    public List<ModifyPropertySpec> convertPropertiesForModification(
            List<PropertyArgument> properties,
            boolean createUuidIfNotExists
    ) throws IOException, StringToPropertyConverterException, PropertyException {
        List<ModifyPropertySpec> modifyPropertySpecs = new ArrayList<>();

        for (PropertyArgument entry : properties) {
            String propertyName = entry.propertyName();
            List<String> propertyValue = entry.values();

            PropertyDescriptor propertyDescriptor = propertyDescriptorService.findPropertyDescriptor(propertyName);

            Property property = null;
            if (propertyValue != null) {
                property = Property.fromUnchecked(propertyDescriptor, stringToProperty(propertyDescriptor, propertyValue, createUuidIfNotExists));
            }

            modifyPropertySpecs.add(new ModifyPropertySpec(
                    propertyDescriptor,
                    property,
                    getModificationType(entry.affinity()),
                    parseOption(entry.predicate())));
        }

        return modifyPropertySpecs;
    }

    public Object stringToProperty(
            PropertyDescriptor propertyDescriptor,
            List<String> propertyValueList,
            boolean createUuidIfNotExists
    ) throws StringToPropertyConverterException, IOException {
        if (propertyValueList.isEmpty()) {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.EmptyList, "property value is empty", propertyDescriptor.name());
        }

        if (!propertyDescriptor.isCollection() && propertyValueList.size() != 1) {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.NotAList, "property '" + propertyDescriptor.name() + "' is not a list", propertyDescriptor.name());
        }

        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            return stringListToProperty(propertyDescriptor, propertyValueList, createUuidIfNotExists);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            return stringSetToProperty(propertyDescriptor, propertyValueList, createUuidIfNotExists);
        } else {
            return singleStringToProperty(propertyDescriptor, propertyValueList.get(0), createUuidIfNotExists);
        }
    }

    public List<Object> stringListToProperty(PropertyDescriptor propertyDescriptor, List<String> propertyValueList, boolean createUuidIfNotExists) throws StringToPropertyConverterException, IOException {
        List<Object> convertedPropertyValueList = new ArrayList<>();
        for (String propertyValue : propertyValueList) {
            convertedPropertyValueList.add(singleStringToProperty(propertyDescriptor, propertyValue, createUuidIfNotExists));
        }
        return convertedPropertyValueList;
    }

    public LinkedHashSet<Object> stringSetToProperty(PropertyDescriptor propertyDescriptor, List<String> propertyValueList, boolean createUuidIfNotExists) throws StringToPropertyConverterException, IOException {
        LinkedHashSet<Object> convertedPropertyValueSet = new LinkedHashSet<>();
        for (String propertyValue : propertyValueList) {
            convertedPropertyValueSet.add(singleStringToProperty(propertyDescriptor, propertyValue, createUuidIfNotExists));
        }
        return convertedPropertyValueSet;
    }

    private boolean stringToBooleanProperty(String propertyValueStr) throws StringToPropertyConverterException {
        String TRUE_STRING = "true";
        String FALSE_STRING = "false";
        if (propertyValueStr.equals(TRUE_STRING)) {
            return true;
        } else if (propertyValueStr.equals(FALSE_STRING)) {
            return false;
        } else {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidBoolean, "Invalid boolean value: " + propertyValueStr, propertyValueStr);
        }
    }

    private Object singleStringToProperty(PropertyDescriptor propertyDescriptor, String propertyValue, boolean createLabelIfNotExist) throws StringToPropertyConverterException, IOException {
        if (propertyDescriptor.type() == PropertyDescriptor.Type.String) {
            return stringToStringProperty(propertyDescriptor, propertyValue, createLabelIfNotExist);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.Boolean) {
            return stringToBooleanProperty(propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.UUID) {
            return stringToUuidProperty(propertyDescriptor, propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.Integer) {
            return stringToIntegerProperty(propertyDescriptor, propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.Date) {
            return stringToDateProperty(propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.Time) {
            return stringToTimeProperty(propertyValue);
        } else {
            throw new RuntimeException();
        }
    }

    private String stringToStringProperty(PropertyDescriptor propertyDescriptor, String propertyValue, boolean createLabelIfNotExist) throws StringToPropertyConverterException, IOException {
        if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.LabelSubtype labelSubtype) {
            boolean labelExists = labelService.labelExists(labelSubtype.labelType(), propertyValue);
            if (!labelExists && createLabelIfNotExist
                    && Utils.yesNo("label '%s' does not exist, create it?".formatted(propertyValue))) {
                labelExists = labelService.createLabel(labelSubtype.labelType(), propertyValue);
            }
            if (labelExists) {
                return propertyValue;
            } else {
                throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.LabelNotFound, "no such label: " + propertyValue, propertyValue);
            }
        } else {
            return propertyValue;
        }
    }

    public Predicate parsePredicate(String predicateStr) throws StringToPropertyConverterException {
        if (predicateStr == null) {
            return null;
        } else if (predicateStr.equals("equals")) {
            return Predicate.EQUALS;
        } else if (predicateStr.equals("contains")) {
            return Predicate.CONTAINS;
        } else if (predicateStr.equals("in")) {
            return Predicate.IN;
        } else if (predicateStr.equals("less")) {
            return Predicate.LESS;
        } else if (predicateStr.equals("less_equal")) {
            return Predicate.LESS_EQUAL;
        } else if (predicateStr.equals("greater")) {
            return Predicate.GREATER;
        } else if (predicateStr.equals("greater_equal")) {
            return Predicate.GREATER_EQUAL;
        } else if (predicateStr.equals("null")) {
            return Predicate.NULL;
        } else if (predicateStr.equals("empty")) {
            return Predicate.EMPTY;
        } else {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidPredicate, "Invalid option: " + predicateStr, predicateStr);
        }
    }

    public ModifyPropertySpec.Option parseOption(String optionStr) throws StringToPropertyConverterException {
        if (optionStr == null) {
            return null;
        } else if (optionStr.equals("remove")) {
            return ModifyPropertySpec.Option.REMOVE;
        } else {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidPropertyOption, "Invalid option: " + optionStr, optionStr);
        }
    }

    private ModifyPropertySpec.ModificationType getModificationType(@NonNull Affinity affinity) {
        switch (affinity) {
            case POSITIVE -> {
                return ModifyPropertySpec.ModificationType.ADD_VALUES;
            }
            case NEGATIVE -> {
                return ModifyPropertySpec.ModificationType.REMOVE_VALUES;
            }
            case NEUTRAL -> {
                return ModifyPropertySpec.ModificationType.SET_VALUE;
            }
        }

        throw new RuntimeException();
    }

    private UUID stringToUuidProperty(PropertyDescriptor propertyDescriptor, String propertyValueStr) throws StringToPropertyConverterException, IOException {
        try {
            return UUID.fromString(propertyValueStr);
        } catch (IllegalArgumentException e1) {
            PropertyDescriptor.Subtype.UUIDSubtype uuidSubtype = propertyDescriptor.getUuidSubtypeUnchecked();
            if (tempIdManager != null && uuidSubtype instanceof PropertyDescriptor.Subtype.TaskSubtype) {
                try {
                    int tempId = Integer.parseInt(propertyValueStr);
                    UUID uuid = tempIdManager.getUUID(tempId);
                    if (uuid == null) {
                        throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.TempIdNotFound, "temporary ID not found: " + propertyValueStr, propertyValueStr);
                    }
                    return uuid;
                } catch (NumberFormatException e) {
                    throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidTempId, "invalid temporary ID (must be a natural number): " + propertyValueStr, propertyValueStr);
                }
            } else {
                throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidUuid, "invalid UUID: " + propertyValueStr, propertyValueStr);
            }
        }
    }

    private Integer stringToIntegerProperty(PropertyDescriptor propertyDescriptor, String propertyValueStr) throws StringToPropertyConverterException, IOException {
        try {
            return Integer.parseInt(propertyValueStr);
        } catch (NumberFormatException e1) {
            PropertyDescriptor.Subtype.OrderedLabelSubtype orderedLabelSubtype = getOrderedLabelSubtype(propertyDescriptor);
            OrderedLabel orderedLabel = orderedLabelService.findOrderedLabel(orderedLabelSubtype.orderedLabelType(), propertyValueStr);

            if (orderedLabel != null) {
                return orderedLabel.value();
            } else {
                throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.OrderedLabelNotFound,
                        "no such ordered label: " + propertyValueStr, propertyValueStr);
            }
        }
    }

    private LocalDate stringToDateProperty(String propertyValueStr) throws StringToPropertyConverterException {
        try {
            return dateTimeParser.parseLocalDate(propertyValueStr);
        } catch (DateTimeParseException e) {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidDate, "invalid date: " + propertyValueStr, propertyValueStr);
        }
    }

    private LocalTime stringToTimeProperty(String propertyValueStr) throws StringToPropertyConverterException {
        try {
            return dateTimeParser.parseLocalTime(propertyValueStr);
        } catch (DateTimeParseException e) {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidTime, "invalid time: " + propertyValueStr, propertyValueStr);
        }
    }

}

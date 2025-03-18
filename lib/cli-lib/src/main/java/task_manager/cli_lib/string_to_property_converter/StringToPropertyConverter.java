package task_manager.cli_lib.string_to_property_converter;

import jakarta.inject.Inject;
import lombok.NonNull;
import task_manager.cli_lib.DateTimeParser;
import task_manager.core.data.Label;
import task_manager.core.data.OrderedLabel;
import task_manager.core.data.Predicate;
import task_manager.core.property.*;
import task_manager.logic.use_case.label.LabelUseCase;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCase;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import task_manager.logic.use_case.temp_id_mapping.TempIDMappingUseCase;
import task_manager.property_lib.Property;
import task_manager.property_lib.PropertyDescriptor;
import task_manager.property_lib.PropertyException;
import task_manager.cli_lib.argument.PropertyArgument;
import task_manager.util.Utils;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.*;

public class StringToPropertyConverter {

    @Inject
    public StringToPropertyConverter(
            LabelUseCase labelUseCase,
            OrderedLabelUseCase orderedLabelUseCase,
            PropertyDescriptorUseCase propertyDescriptorUseCase,
            TempIDMappingUseCase tempIDMappingUseCase
    ) {
        this.labelUseCase = labelUseCase;
        this.orderedLabelUseCase = orderedLabelUseCase;
        this.propertyDescriptorUseCase = propertyDescriptorUseCase;
        this.tempIDMappingUseCase = tempIDMappingUseCase;
        this.dateTimeParser = new DateTimeParser();
    }

    public List<FilterPropertySpec> convertPropertiesForFiltering(
            @NonNull List<PropertyArgument> properties,
            boolean createUuidIfNotExists
    ) throws IOException, StringToPropertyConverterException, PropertyException {
        List<FilterPropertySpec> filterPropertySpecs = new ArrayList<>();

        for (PropertyArgument entry : properties) {
            String propertyName = entry.propertyName();
            List<String> propertyValue = entry.values();

            if (propertyValue == null) {
                throw new StringToPropertyConverterException(
                        StringToPropertyConverterException.Type.MissingPropertyValue,
                        "Missing property value for property '" + propertyName + "'",
                        propertyName);
            }

            PropertyDescriptor propertyDescriptor = propertyDescriptorUseCase.findPropertyDescriptor(propertyName);
            Property property = Property.fromUnchecked(propertyDescriptor, stringToProperty(propertyDescriptor, propertyValue, createUuidIfNotExists));

            filterPropertySpecs.add(new FilterPropertySpec(property, entry.affinity() == Affinity.NEGATIVE, parsePredicate(entry.option())));
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

            PropertyDescriptor propertyDescriptor = propertyDescriptorUseCase.findPropertyDescriptor(propertyName);

            Property property = null;
            if (propertyValue != null) {
                property = Property.fromUnchecked(propertyDescriptor, stringToProperty(propertyDescriptor, propertyValue, createUuidIfNotExists));
            }

            modifyPropertySpecs.add(new ModifyPropertySpec(
                    propertyDescriptor,
                    property,
                    getModificationType(entry.affinity()),
                    parseOption(entry.option())));
        }

        return modifyPropertySpecs;
    }

    public Object stringToProperty(PropertyDescriptor propertyDescriptor, List<String> propertyValueList, boolean createUuidIfNotExists) throws StringToPropertyConverterException, IOException {
        if (propertyValueList.isEmpty()) {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.EmptyList, "Property value is empty", propertyDescriptor.name());
        }

        if (!propertyDescriptor.isCollection() && propertyValueList.size() != 1) {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.NotAList, "Property '" + propertyDescriptor.name() + "' is not a list", propertyDescriptor.name());
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

    private Object singleStringToProperty(PropertyDescriptor propertyDescriptor, String propertyValue, boolean createUuidIfNotExists) throws StringToPropertyConverterException, IOException {
        if (propertyDescriptor.type() == PropertyDescriptor.Type.String) {
            return stringToStringProperty(propertyDescriptor, propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.Boolean) {
            return stringToBooleanProperty(propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.UUID) {
            return stringToUuidProperty(propertyDescriptor, propertyValue, createUuidIfNotExists);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.Integer) {
            return stringToIntegerProperty(propertyDescriptor, propertyValue);
        } else {
            throw new RuntimeException("This should not happen");
        }
    }

    private String stringToStringProperty(PropertyDescriptor propertyDescriptor, String propertyValue) throws StringToPropertyConverterException {
        if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.DateSubtype) {
            return stringToDateProperty(propertyValue);
        } else if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.TimeSubtype) {
            return stringToTimeProperty(propertyValue);
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
        } else if (predicateStr.equals("less")) {
            return Predicate.LESS;
        } else if (predicateStr.equals("less_equal")) {
            return Predicate.LESS_EQUAL;
        } else if (predicateStr.equals("greater")) {
            return Predicate.GREATER;
        } else if (predicateStr.equals("greater_equal")) {
            return Predicate.GREATER_EQUAL;
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
            case POSITIVE -> { return ModifyPropertySpec.ModificationType.ADD_VALUES; }
            case NEGATIVE -> { return ModifyPropertySpec.ModificationType.REMOVE_VALUES; }
            case NEUTRAL -> { return ModifyPropertySpec.ModificationType.SET_VALUE; }
        }

        throw new RuntimeException();
    }

    private UUID stringToUuidProperty(PropertyDescriptor propertyDescriptor, String propertyValueStr, boolean createUuidIfNotExists) throws StringToPropertyConverterException, IOException {
        try {
            return UUID.fromString(propertyValueStr);
        } catch (IllegalArgumentException e1) {
            PropertyDescriptor.Subtype.UUIDSubtype uuidSubtype = propertyDescriptor.getUuidSubtypeUnchecked();
            if (uuidSubtype instanceof PropertyDescriptor.Subtype.LabelSubtype labelSubtype) {
                Label label = labelUseCase.findLabel(labelSubtype.labelType(), propertyValueStr);
                if (label == null && createUuidIfNotExists
                        && Utils.yesNo("Label '" + propertyValueStr + "' does not exist. Do you want to create it?")) {
                    label = labelUseCase.createLabel(labelSubtype.labelType(), propertyValueStr);
                }
                if (label != null) {
                    return label.uuid();
                } else {
                    throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.LabelNotFound, "Label not found: " + labelSubtype.labelType(), labelSubtype.labelType());
                }
            } else if (tempIDMappingUseCase != null && uuidSubtype instanceof PropertyDescriptor.Subtype.TaskSubtype) {
                try {
                    int tempId = Integer.parseInt(propertyValueStr);
                    UUID uuid = tempIDMappingUseCase.getUUID(tempId);
                    if (uuid == null) {
                        throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.TempIdNotFound, "Temporary ID not found: " + propertyValueStr, propertyValueStr);
                    }
                    return uuid;
                } catch (NumberFormatException e) {
                    throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidTempId, "Invalid temporary ID (must be a natural number): " + propertyValueStr, propertyValueStr);
                }
            } else {
                throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidUuid, "Invalid UUID: " + propertyValueStr, propertyValueStr);
            }
        }
    }

    private Integer stringToIntegerProperty(PropertyDescriptor propertyDescriptor, String propertyValueStr) throws StringToPropertyConverterException, IOException {
        try {
            return Integer.parseInt(propertyValueStr);
        } catch (NumberFormatException e1) {
            PropertyDescriptor.Subtype.OrderedLabelSubtype orderedLabelSubtype = getOrderedLabelSubtype(propertyDescriptor);
            OrderedLabel orderedLabel = orderedLabelUseCase.findOrderedLabel(orderedLabelSubtype.orderedLabelType(), propertyValueStr);

            if (orderedLabel != null) {
                return orderedLabel.value();
            } else {
                throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.OrderedLabelNotFound,
                        "Ordered label not found: " + orderedLabelSubtype.orderedLabelType(), orderedLabelSubtype.orderedLabelType());
            }
        }
    }

    private String stringToDateProperty(String propertyValueStr) throws StringToPropertyConverterException {
        try {
            return dateTimeParser.parseLocalDate(propertyValueStr).format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidDate, "Invalid date: " + propertyValueStr, propertyValueStr);
        }
    }

    private String stringToTimeProperty(String propertyValueStr) throws StringToPropertyConverterException {
        try {
            return dateTimeParser.parseLocalTime(propertyValueStr).format(java.time.format.DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException e) {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.InvalidTime, "Invalid time: " + propertyValueStr, propertyValueStr);
        }
    }

    private static PropertyDescriptor.Subtype.OrderedLabelSubtype getOrderedLabelSubtype(PropertyDescriptor propertyDescriptor) throws StringToPropertyConverterException {
        PropertyDescriptor.Subtype.IntegerSubtype integerSubtype = propertyDescriptor.getIntegerSubtypeUnchecked();
        if (!(integerSubtype instanceof PropertyDescriptor.Subtype.OrderedLabelSubtype)) {
            throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.NoAssociatedLabel,
                    "Property '" + propertyDescriptor.name() + "' is not a label", propertyDescriptor.name());
        }

        return (PropertyDescriptor.Subtype.OrderedLabelSubtype)integerSubtype;
    }

    private final PropertyDescriptorUseCase propertyDescriptorUseCase;
    private final LabelUseCase labelUseCase;
    private final OrderedLabelUseCase orderedLabelUseCase;
    private final TempIDMappingUseCase tempIDMappingUseCase;
    private final DateTimeParser dateTimeParser;

}

package task_manager.ui.cli.command.string_to_property_converter;

import jakarta.inject.Inject;
import lombok.NonNull;
import task_manager.core.data.Label;
import task_manager.core.data.OrderedLabel;
import task_manager.core.data.Predicate;
import task_manager.core.property.*;
import task_manager.logic.use_case.label.LabelUseCase;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCase;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import task_manager.property_lib.Property;
import task_manager.property_lib.PropertyDescriptor;
import task_manager.property_lib.PropertyException;
import task_manager.ui.cli.Util;
import task_manager.ui.cli.argument.PropertyArgument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class StringToPropertyConverter {

    private final LabelUseCase labelUseCase;

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
            return propertyValue;
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

    @Inject
    public StringToPropertyConverter(
            LabelUseCase labelUseCase,
            OrderedLabelUseCase orderedLabelUseCase,
            PropertyDescriptorUseCase propertyDescriptorUseCase
    ) {
        this.labelUseCase = labelUseCase;
        this.orderedLabelUseCase = orderedLabelUseCase;
        this.propertyDescriptorUseCase = propertyDescriptorUseCase;
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
            PropertyDescriptor.UUIDExtra uuidExtra = propertyDescriptor.getUuidExtraUnchecked();
            if (uuidExtra == null || uuidExtra.labelName() == null) {
                throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.NoAssociatedLabel,
                        "UUID property '" + propertyDescriptor.name() + "' does not have an associated label", propertyDescriptor.name());
            }

            Label label = labelUseCase.findLabel(uuidExtra.labelName(), propertyValueStr);
            if (label == null && createUuidIfNotExists
                    && Util.yesNo("Label '" + propertyValueStr + "' does not exist. Do you want to create it?")) {
                label = labelUseCase.createLabel(uuidExtra.labelName(), propertyValueStr);
            }
            if (label != null) {
                return label.uuid();
            } else {
                throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.LabelNotFound, "Label not found: " + uuidExtra.labelName(), uuidExtra.labelName());
            }
        }
    }

    private Integer stringToIntegerProperty(PropertyDescriptor propertyDescriptor, String propertyValueStr) throws StringToPropertyConverterException, IOException {
        try {
            return Integer.parseInt(propertyValueStr);
        } catch (NumberFormatException e1) {
            PropertyDescriptor.IntegerExtra integerExtra = propertyDescriptor.getIntegerExtraUnchecked();
            if (integerExtra == null || integerExtra.orderedLabelName() == null) {
                throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.NoAssociatedLabel,
                        "UUID property '" + propertyDescriptor.name() + "' does not have an associated label", propertyDescriptor.name());
            }

            OrderedLabel orderedLabel = orderedLabelUseCase.findOrderedLabel(integerExtra.orderedLabelName(), propertyValueStr);

            if (orderedLabel != null) {
                return orderedLabel.value();
            } else {
                throw new StringToPropertyConverterException(StringToPropertyConverterException.Type.OrderedLabelNotFound,
                        "Label not found: " + integerExtra.orderedLabelName(), integerExtra.orderedLabelName());
            }
        }
    }
    private final PropertyDescriptorUseCase propertyDescriptorUseCase;
    private final OrderedLabelUseCase orderedLabelUseCase;

}

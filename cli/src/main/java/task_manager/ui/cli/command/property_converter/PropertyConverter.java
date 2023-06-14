package task_manager.ui.cli.command.property_converter;

import jakarta.inject.Inject;
import task_manager.data.Label;
import task_manager.logic.use_case.PropertyDescriptorUseCase;
import task_manager.property.*;
import task_manager.repository.LabelRepository;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.ui.cli.Util;
import task_manager.property.PropertySpec;
import task_manager.ui.cli.argument.PropertyArgument;
import task_manager.util.UUIDGenerator;

import java.io.IOException;
import java.util.*;

public class PropertyConverter {

    @Inject
    public PropertyConverter(
            LabelRepositoryFactory labelRepositoryFactory, PropertyDescriptorUseCase propertyDescriptorUseCase, UUIDGenerator uuidGenerator) {
        this.labelRepositoryFactory = labelRepositoryFactory;
        this.propertyDescriptorUseCase = propertyDescriptorUseCase;
        this.uuidGenerator = uuidGenerator;
    }

    public List<PropertySpec> convertProperties(List<PropertyArgument> properties, boolean createUuidIfNotExists) throws IOException, PropertyConverterException, PropertyException {
        List<PropertySpec> propertySpecs = new ArrayList<>();

        for (PropertyArgument entry : properties) {
            String propertyName = entry.propertyName();
            List<String> propertyValue = entry.values();

            PropertyDescriptor propertyDescriptor = propertyDescriptorUseCase.getPropertyDescriptor(propertyName);

            Object property = stringToProperty(propertyDescriptor, propertyValue, createUuidIfNotExists);
            propertySpecs.add(new PropertySpec(Property.fromUnchecked(propertyDescriptor, property), entry.affinity(), parsePredicate(entry.predicate())));
        }

        return propertySpecs;
    }

    public Object stringToProperty(PropertyDescriptor propertyDescriptor, List<String> propertyValueList, boolean createUuidIfNotExists) throws PropertyConverterException, IOException {
        if (propertyValueList.isEmpty()) {
            throw new PropertyConverterException(PropertyConverterException.Type.EmptyList, propertyDescriptor, null);
        }

        if (!propertyDescriptor.isCollection() && propertyValueList.size() != 1) {
            throw new PropertyConverterException(PropertyConverterException.Type.NotAList, propertyDescriptor, null);
        }

        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            return stringListToProperty(propertyDescriptor, propertyValueList, createUuidIfNotExists);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            return stringSetToProperty(propertyDescriptor, propertyValueList, createUuidIfNotExists);
        } else {
            return singleStringToProperty(propertyDescriptor, propertyValueList.get(0), createUuidIfNotExists);
        }
    }

    public List<Object> stringListToProperty(PropertyDescriptor propertyDescriptor, List<String> propertyValueList, boolean createUuidIfNotExists) throws PropertyConverterException, IOException {
        List<Object> convertedPropertyValueList = new ArrayList<>();
        for (String propertyValue : propertyValueList) {
            convertedPropertyValueList.add(singleStringToProperty(propertyDescriptor, propertyValue, createUuidIfNotExists));
        }
        return convertedPropertyValueList;
    }

    public LinkedHashSet<Object> stringSetToProperty(PropertyDescriptor propertyDescriptor, List<String> propertyValueList, boolean createUuidIfNotExists) throws PropertyConverterException, IOException {
        LinkedHashSet<Object> convertedPropertyValueSet = new LinkedHashSet<>();
        for (String propertyValue : propertyValueList) {
            convertedPropertyValueSet.add(singleStringToProperty(propertyDescriptor, propertyValue, createUuidIfNotExists));
        }
        return convertedPropertyValueSet;
    }

    private Object singleStringToProperty(PropertyDescriptor propertyDescriptor, String propertyValue, boolean createUuidIfNotExists) throws PropertyConverterException, IOException {
        if (propertyDescriptor.type() == PropertyDescriptor.Type.String) {
            return stringToStringProperty(propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.Boolean) {
            return stringToBooleanProperty(propertyDescriptor, propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.UUID) {
            return stringToUuidProperty(propertyDescriptor, propertyValue, createUuidIfNotExists);
        } else {
            throw new RuntimeException("This should not happen");
        }
    }

    private Object stringToStringProperty(String propertyValueStr) {
        return propertyValueStr;
    }

    private boolean stringToBooleanProperty(PropertyDescriptor propertyDescriptor, String propertyValueStr) throws PropertyConverterException {
        String TRUE_STRING = "true";
        String FALSE_STRING = "false";
        if (propertyValueStr.equals(TRUE_STRING)) {
            return true;
        } else if (propertyValueStr.equals(FALSE_STRING)) {
            return false;
        } else {
            throw new PropertyConverterException(PropertyConverterException.Type.InvalidBoolean, propertyDescriptor, propertyValueStr);
        }
    }

    private UUID stringToUuidProperty(PropertyDescriptor propertyDescriptor, String propertyValueStr, boolean createUuidIfNotExists) throws PropertyConverterException, IOException {
        try {
            return UUID.fromString(propertyValueStr);
        } catch (IllegalArgumentException e1) {
            LabelRepository labelRepository = labelRepositoryFactory.getLabelRepository(propertyDescriptor.name());
            Label label = labelRepository.find(propertyValueStr);
            if (label == null && createUuidIfNotExists
                    && Util.yesNo("Label '" + propertyValueStr + "' does not exist. Do you want to create it?")) {
                label = labelRepository.create(new Label(uuidGenerator.getUUID(), propertyValueStr));
            }
            if (label != null) {
                return label.uuid();
            } else {
                throw new PropertyConverterException(PropertyConverterException.Type.LabelNotFound, propertyDescriptor, propertyValueStr);
            }
        }
    }

    public PropertySpec.Predicate parsePredicate(String predicateStr) throws PropertyConverterException {
        if (predicateStr == null) {
            return null;
        } else if (predicateStr.equals("equals")) {
            return PropertySpec.Predicate.EQUALS;
        } else if (predicateStr.equals("contains")) {
            return PropertySpec.Predicate.CONTAINS;
        } else {
            throw new PropertyConverterException(predicateStr);
        }
    }

    private final LabelRepositoryFactory labelRepositoryFactory;
    private final PropertyDescriptorUseCase propertyDescriptorUseCase;
    private final UUIDGenerator uuidGenerator;

}

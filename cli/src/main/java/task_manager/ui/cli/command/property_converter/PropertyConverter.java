package task_manager.ui.cli.command.property_converter;

import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.Triple;
import task_manager.data.Label;
import task_manager.logic.use_case.PropertyDescriptorUseCase;
import task_manager.property.*;
import task_manager.repository.LabelRepository;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.ui.cli.Util;
import task_manager.property.PropertySpec;
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

    public List<PropertySpec> convertProperties(List<Triple<PropertySpec.Affinity, String, List<String>>> properties) throws IOException, PropertyConverterException {
        List<PropertySpec> propertySpecs = new ArrayList<>();

        for (Triple<PropertySpec.Affinity, String, List<String>> entry : properties) {
            String propertyName = entry.getMiddle();
            List<String> propertyValue = entry.getRight();

            PropertyDescriptor propertyDescriptor = propertyDescriptorUseCase.getPropertyDescriptor(propertyName);

            Object property = stringToProperty(propertyDescriptor, propertyValue);
            propertySpecs.add(new PropertySpec(Property.fromUnchecked(propertyDescriptor, property), entry.getLeft()));
        }

        return propertySpecs;
    }

    public Object stringToProperty(PropertyDescriptor propertyDescriptor, List<String> propertyValueList) throws PropertyConverterException, IOException {
        if (propertyValueList.isEmpty()) {
            throw new PropertyConverterException(PropertyConverterException.Type.EmptyList, propertyDescriptor, null);
        }

        if (!propertyDescriptor.isCollection() && propertyValueList.size() != 1) {
            throw new PropertyConverterException(PropertyConverterException.Type.NotAList, propertyDescriptor, null);
        }

        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            return stringListToProperty(propertyDescriptor, propertyValueList);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            return stringSetToProperty(propertyDescriptor, propertyValueList);
        } else {
            return singleStringToProperty(propertyDescriptor, propertyValueList.get(0));
        }
    }

    public List<Object> stringListToProperty(PropertyDescriptor propertyDescriptor, List<String> propertyValueList) throws PropertyConverterException, IOException {
        List<Object> convertedPropertyValueList = new ArrayList<>();
        for (String propertyValue : propertyValueList) {
            convertedPropertyValueList.add(singleStringToProperty(propertyDescriptor, propertyValue));
        }
        return convertedPropertyValueList;
    }

    public LinkedHashSet<Object> stringSetToProperty(PropertyDescriptor propertyDescriptor, List<String> propertyValueList) throws PropertyConverterException, IOException {
        LinkedHashSet<Object> convertedPropertyValueSet = new LinkedHashSet<>();
        for (String propertyValue : propertyValueList) {
            convertedPropertyValueSet.add(singleStringToProperty(propertyDescriptor, propertyValue));
        }
        return convertedPropertyValueSet;
    }

    private Object singleStringToProperty(PropertyDescriptor propertyDescriptor, String propertyValue) throws PropertyConverterException, IOException {
        if (propertyDescriptor.type() == PropertyDescriptor.Type.String) {
            return stringToStringProperty(propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.Boolean) {
            return stringToBooleanProperty(propertyDescriptor, propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.UUID) {
            return stringToUuidProperty(propertyDescriptor, propertyValue);
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

    private UUID stringToUuidProperty(PropertyDescriptor propertyDescriptor, String propertyValueStr) throws PropertyConverterException, IOException {
        try {
            return UUID.fromString(propertyValueStr);
        } catch (IllegalArgumentException e1) {
            LabelRepository labelRepository = labelRepositoryFactory.getLabelRepository(propertyDescriptor.name());
            Label label = labelRepository.find(propertyValueStr);
            if (label == null && Util.yesNo("Label '" + propertyValueStr + "' does not exist. Do you want to create it?")) {
                label = labelRepository.create(new Label(uuidGenerator.getUUID(), propertyValueStr));
            }
            if (label != null) {
                return label.uuid();
            } else {
                throw new PropertyConverterException(PropertyConverterException.Type.LabelNotFound, propertyDescriptor, propertyValueStr);
            }
        }
    }

    private final LabelRepositoryFactory labelRepositoryFactory;
    private final PropertyDescriptorUseCase propertyDescriptorUseCase;
    private final UUIDGenerator uuidGenerator;

}

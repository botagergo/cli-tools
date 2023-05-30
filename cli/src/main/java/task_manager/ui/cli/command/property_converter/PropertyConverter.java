package task_manager.ui.cli.command.property_converter;

import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import task_manager.data.Label;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyOwner;
import task_manager.logic.use_case.PropertyDescriptorUseCase;
import task_manager.repository.LabelRepository;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.ui.cli.Util;
import task_manager.util.UUIDGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PropertyConverter {

    @Inject
    public PropertyConverter(
            LabelRepositoryFactory labelRepositoryFactory, PropertyDescriptorUseCase propertyDescriptorUseCase, UUIDGenerator uuidGenerator) {
        this.labelRepositoryFactory = labelRepositoryFactory;
        this.propertyDescriptorUseCase = propertyDescriptorUseCase;
        this.uuidGenerator = uuidGenerator;
    }

    public void convertProperties(List<Pair<String, List<String>>> properties, PropertyOwner propertyOwner, PropertyManager propertyManager) throws IOException, PropertyConverterException, PropertyException {
        List<Pair<String, Object>> changes = new ArrayList<>();

        for (Pair<String, List<String>> entry : properties) {
            String propertyName = entry.getKey();
            List<String> propertyValue = entry.getValue();

            PropertyDescriptor propertyDescriptor = propertyDescriptorUseCase.getPropertyDescriptor(propertyName);

            Object property = stringToProperty(propertyDescriptor, propertyValue);
            changes.add(Pair.of(propertyName, property));
        }

        for (Pair<String, Object> change : changes) {
            propertyManager.setProperty(propertyOwner, change.getKey(), change.getValue());
        }
    }

    public Object stringToProperty(PropertyDescriptor propertyDescriptor, List<String> propertyValueList) throws PropertyConverterException, IOException {
        if (propertyValueList.isEmpty()) {
            throw new PropertyConverterException(PropertyConverterException.Type.EmptyList, propertyDescriptor, null);
        }

        if (!propertyDescriptor.isList() && propertyValueList.size() != 1) {
            throw new PropertyConverterException(PropertyConverterException.Type.NotAList, propertyDescriptor, null);
        }

        if (propertyDescriptor.isList()) {
            return stringListToProperty(propertyDescriptor, propertyValueList);
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

    private String stringToStringProperty(String propertyValueStr) {
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

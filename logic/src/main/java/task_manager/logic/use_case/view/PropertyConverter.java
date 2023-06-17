package task_manager.logic.use_case.view;

import jakarta.inject.Inject;
import task_manager.data.Label;
import task_manager.property.PropertyDescriptor;
import task_manager.repository.LabelRepository;
import task_manager.repository.LabelRepositoryFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class PropertyConverter {

    @Inject
    public PropertyConverter(LabelRepositoryFactory labelRepositoryFactory) {
        this.labelRepositoryFactory = labelRepositoryFactory;
    }

    public Object convertProperty(PropertyDescriptor propertyDescriptor, List<Object> propertyValueList) throws PropertyConverterException, IOException {
        if (propertyValueList.isEmpty()) {
            throw new PropertyConverterException(PropertyConverterException.Type.EmptyList, propertyDescriptor, null);
        }

        if (!propertyDescriptor.isCollection() && propertyValueList.size() != 1) {
            throw new PropertyConverterException(PropertyConverterException.Type.NotACollection, propertyDescriptor, null);
        }

        if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            return convertPropertyList(propertyDescriptor, propertyValueList);
        } else if (propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            return convertPropertySet(propertyDescriptor, propertyValueList);
        } else {
            return convertSingleProperty(propertyDescriptor, propertyValueList.get(0));
        }
    }

    public List<Object> convertPropertyList(PropertyDescriptor propertyDescriptor, List<Object> propertyValueList) throws PropertyConverterException, IOException {
        List<Object> convertedPropertyValueList = new ArrayList<>();
        for (Object propertyValue : propertyValueList) {
            convertedPropertyValueList.add(convertSingleProperty(propertyDescriptor, propertyValue));
        }
        return convertedPropertyValueList;
    }

    public LinkedHashSet<Object> convertPropertySet(PropertyDescriptor propertyDescriptor, List<Object> propertyValueList) throws PropertyConverterException, IOException {
        LinkedHashSet<Object> convertedPropertyValueSet = new LinkedHashSet<>();
        for (Object propertyValue : propertyValueList) {
            convertedPropertyValueSet.add(convertSingleProperty(propertyDescriptor, propertyValue));
        }
        return convertedPropertyValueSet;
    }

    private Object convertSingleProperty(PropertyDescriptor propertyDescriptor, Object propertyValue) throws PropertyConverterException, IOException {
        if (propertyDescriptor.type() == PropertyDescriptor.Type.String
            || propertyDescriptor.type() == PropertyDescriptor.Type.Boolean) {
            return propertyValue;
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.UUID) {
            return convertUuidProperty(propertyDescriptor, propertyValue);
        } else {
            throw new RuntimeException("This should not happen");
        }
    }

    private UUID convertUuidProperty(PropertyDescriptor propertyDescriptor, Object propertyValue) throws PropertyConverterException, IOException {
        String propertyValueStr = (String) propertyValue;

        try {
            return UUID.fromString(propertyValueStr);
        } catch (IllegalArgumentException e1) {
            LabelRepository labelRepository = labelRepositoryFactory.getLabelRepository(propertyDescriptor.name());
            Label label = labelRepository.find(propertyValueStr);
            if (label == null) {
                throw new PropertyConverterException(PropertyConverterException.Type.LabelNotFound, propertyDescriptor, propertyValueStr);
            }
            return label.uuid();
        }
    }

    private final LabelRepositoryFactory labelRepositoryFactory;

}

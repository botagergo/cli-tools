package cli_tools.common.property_converter;

import jakarta.inject.Inject;
import cli_tools.common.core.data.Label;
import cli_tools.common.core.repository.LabelRepository;
import cli_tools.common.core.repository.LabelRepositoryFactory;
import cli_tools.common.property_lib.PropertyDescriptor;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PropertyConverter {

    @Inject
    public PropertyConverter(LabelRepositoryFactory labelRepositoryFactory) {
        this.labelRepositoryFactory = labelRepositoryFactory;
    }

    public List<Object> convertProperty(@NonNull PropertyDescriptor propertyDescriptor, @NonNull List<Object> propertyValueList) throws PropertyConverterException, IOException {
        List<Object> convertedPropertyValueList = new ArrayList<>();
        for (Object propertyValue : propertyValueList) {
            convertedPropertyValueList.add(convertSingleProperty(propertyDescriptor, propertyValue));
        }
        return convertedPropertyValueList;
    }

    private Object convertSingleProperty(PropertyDescriptor propertyDescriptor, Object propertyValue) throws PropertyConverterException, IOException {
        if (propertyDescriptor.type() == PropertyDescriptor.Type.String
                || propertyDescriptor.type() == PropertyDescriptor.Type.Boolean
                || propertyDescriptor.type() == PropertyDescriptor.Type.Integer) {
            return propertyValue;
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.UUID) {
            return convertUuidProperty(propertyDescriptor, propertyValue);
        } else {
            throw new RuntimeException();
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

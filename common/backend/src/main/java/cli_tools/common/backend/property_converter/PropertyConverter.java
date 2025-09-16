package cli_tools.common.backend.property_converter;

import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.backend.ordered_label.service.OrderedLabelService;
import cli_tools.common.property_lib.PropertyDescriptor;
import jakarta.inject.Inject;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PropertyConverter {

    private final OrderedLabelService orderedLabelService;

    @Inject
    public PropertyConverter(
            OrderedLabelService orderedLabelService) {
        this.orderedLabelService = orderedLabelService;
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
                || propertyDescriptor.type() == PropertyDescriptor.Type.Boolean) {
            return propertyValue;
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.Integer) {
            return convertIntegerProperty(propertyDescriptor, propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.UUID) {
            return convertUuidProperty(propertyValue);
        } else {
            throw new RuntimeException();
        }
    }

    private Integer convertIntegerProperty(PropertyDescriptor propertyDescriptor, Object propertyValue) throws PropertyConverterException, IOException {
        if (propertyValue instanceof Integer integerPropertyValue) {
            return integerPropertyValue;
        } else if (propertyValue instanceof String stringPropertyValue
                && propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.OrderedLabelSubtype(
                String orderedLabelType
        )) {
            OrderedLabel orderedLabel = orderedLabelService.findOrderedLabel(orderedLabelType, stringPropertyValue);
            if (orderedLabel == null) {
                throw new PropertyConverterException(PropertyConverterException.Type.LabelNotFound, propertyDescriptor, stringPropertyValue);
            }
            return orderedLabel.value();
        } else {
            throw new PropertyConverterException(PropertyConverterException.Type.IllegalType, propertyDescriptor, propertyValue);
        }
    }

    private UUID convertUuidProperty(Object propertyValue) {
        String propertyValueStr = (String) propertyValue;
        return UUID.fromString(propertyValueStr);
    }

}

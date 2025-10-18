package cli_tools.common.backend.property_converter;

import cli_tools.common.backend.label.service.LabelService;
import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.backend.ordered_label.service.OrderedLabelService;
import cli_tools.common.property_lib.PropertyDescriptor;
import jakarta.inject.Inject;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PropertyConverter {

    private final LabelService labelService;
    private final OrderedLabelService orderedLabelService;

    @Inject
    public PropertyConverter(
            LabelService labelService,
            OrderedLabelService orderedLabelService) {
        this.labelService = labelService;
        this.orderedLabelService = orderedLabelService;
    }

    public List<Object> convertProperty(@NonNull PropertyDescriptor propertyDescriptor, @NonNull List<Object> propertyValueList) throws ServiceException {
        List<Object> convertedPropertyValueList = new ArrayList<>();
        for (Object propertyValue : propertyValueList) {
            convertedPropertyValueList.add(convertSingleProperty(propertyDescriptor, propertyValue));
        }
        return convertedPropertyValueList;
    }

    private Object convertSingleProperty(PropertyDescriptor propertyDescriptor, Object propertyValue) throws ServiceException {
        if (propertyDescriptor.type() == PropertyDescriptor.Type.String
                || propertyDescriptor.type() == PropertyDescriptor.Type.Boolean) {
            return propertyValue;
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.Integer) {
            return convertIntegerProperty(propertyDescriptor, propertyValue);
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.UUID) {
            return convertUuidProperty(propertyDescriptor, propertyValue);
        } else {
            throw new RuntimeException();
        }
    }

    private Integer convertIntegerProperty(PropertyDescriptor propertyDescriptor, Object propertyValue) throws ServiceException {
        if (propertyValue instanceof Integer integerPropertyValue) {
            return integerPropertyValue;
        } else if (propertyValue instanceof String stringPropertyValue
                && propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.OrderedLabelSubtype(
                String orderedLabelType
        )) {
            OrderedLabel orderedLabel = orderedLabelService.findOrderedLabel(orderedLabelType, stringPropertyValue);
            if (orderedLabel == null) {
                throw new PropertyConverterException("Ordered label does not exist: %s (%s)".formatted(stringPropertyValue, orderedLabelType), null);
            }
            return orderedLabel.value();
        } else {
            throw new PropertyConverterException("Illegal type for ordered label: %s".formatted(propertyValue.getClass().toString()), null);
        }
    }

    private UUID convertUuidProperty(PropertyDescriptor propertyDescriptor, Object propertyValue) throws ServiceException {
        String propertyValueStr = (String) propertyValue;
        try {
            return UUID.fromString(propertyValueStr);
        } catch (IllegalArgumentException e) {
            if (propertyDescriptor.subtype() instanceof PropertyDescriptor.Subtype.LabelSubtype(String labelType)) {
                return labelService.findLabel(labelType, propertyValueStr);
            } else {
                throw new PropertyConverterException("Invalid UUID: %s".formatted(propertyValueStr), e);
            }
        }
    }

}

package cli_tools.common.property_converter;

import lombok.Getter;
import cli_tools.common.property_lib.PropertyDescriptor;

@Getter
public class PropertyConverterException extends Exception {

    public PropertyConverterException(Type exceptionType, PropertyDescriptor propertyDescriptor, Object propertyValue) {
        super(getMsg(exceptionType, propertyDescriptor, propertyValue));

        this.exceptionType = exceptionType;
        this.propertyDescriptor = propertyDescriptor;
        this.propertyValue = propertyValue;
        this.predicate = null;
    }

    private static String getMsg(Type exceptionType, PropertyDescriptor propertyDescriptor, Object propertyValue) {
        if (exceptionType == Type.NotACollection) {
            return "Property '" + propertyDescriptor.name() + "' is not a collection";
        } else if (exceptionType == Type.EmptyList) {
            return "Property value is empty";
        } else if (exceptionType == Type.LabelNotFound) {
            return "Label not found: " + propertyValue;
        } else {
            return null;
        }
    }

    final Type exceptionType;
    final PropertyDescriptor propertyDescriptor;
    final Object propertyValue;
    final String predicate;

    public enum Type {
        NotACollection, EmptyList, LabelNotFound
    }

}

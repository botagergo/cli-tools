package task_manager.ui.cli.command.property_converter;

import lombok.Getter;
import task_manager.property.PropertyDescriptor;

public class PropertyConverterException extends Exception {

    public PropertyConverterException(Type exceptionType, PropertyDescriptor propertyDescriptor, Object propertyValue) {
        super(getMsg(exceptionType, propertyDescriptor, propertyValue));

        this.exceptionType = exceptionType;
        this.propertyDescriptor = propertyDescriptor;
        this.propertyValue = propertyValue;
    }

    private static String getMsg(Type exceptionType, PropertyDescriptor propertyDescriptor, Object propertyValue) {
        if (exceptionType == Type.NotAList) {
            return "Property '" + propertyDescriptor.name() + "' is not a list";
        } else if (exceptionType == Type.EmptyList) {
            return "Property value is empty";
        } else if (exceptionType == Type.LabelNotFound) {
            return "UUID not found: " + propertyValue;
        } else if (exceptionType == Type.InvalidBoolean) {
            return "Invalid boolean value: " + propertyValue;
        } else {
            return null;
        }
    }

    @Getter final Type exceptionType;
    @Getter final PropertyDescriptor propertyDescriptor;
    @Getter final Object propertyValue;

    public enum Type {
        NotAList, EmptyList, InvalidBoolean, LabelNotFound
    }

}

package task_manager.ui.cli.command.property_converter;

import lombok.Getter;
import task_manager.property.PropertyDescriptor;

public class PropertyConverterException extends Exception {

    public PropertyConverterException(Type exceptionType, PropertyDescriptor propertyDescriptor, Object propertyValue) {
        super(getMsg(exceptionType, propertyDescriptor, propertyValue, null));

        this.exceptionType = exceptionType;
        this.propertyDescriptor = propertyDescriptor;
        this.propertyValue = propertyValue;
        this.predicate = null;
    }

    public PropertyConverterException(String predicate) {
        super(getMsg(Type.InvalidPredicate, null, null, predicate));

        this.exceptionType = Type.InvalidPredicate;
        this.propertyDescriptor = null;
        this.propertyValue = null;
        this.predicate = predicate;
    }

    private static String getMsg(Type exceptionType, PropertyDescriptor propertyDescriptor, Object propertyValue, String predicate) {
        if (exceptionType == Type.NotAList) {
            return "Property '" + propertyDescriptor.name() + "' is not a list";
        } else if (exceptionType == Type.EmptyList) {
            return "Property value is empty";
        } else if (exceptionType == Type.LabelNotFound) {
            return "Label not found: " + propertyValue;
        } else if (exceptionType == Type.InvalidBoolean) {
            return "Invalid boolean value: " + propertyValue;
        } else if (exceptionType == Type.InvalidPredicate) {
            return "Invalid predicate: " + predicate;
        }else {
            return null;
        }
    }

    @Getter final Type exceptionType;
    @Getter final PropertyDescriptor propertyDescriptor;
    @Getter final Object propertyValue;
    @Getter final String predicate;

    public enum Type {
        NotAList, EmptyList, InvalidBoolean, LabelNotFound, InvalidPredicate
    }

}

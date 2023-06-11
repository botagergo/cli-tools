package task_manager.property;

import lombok.Getter;

public class PropertyException extends Exception {

    public PropertyException(Type exceptionType, String propertyName,
            PropertyDescriptor propertyDescriptor, Object propertyValue,
            PropertyDescriptor.Type requestedType) {
        super(getMsg(exceptionType, propertyName, propertyDescriptor, propertyValue,
                requestedType));

        this.exceptionType = exceptionType;
        this.propertyName = propertyName;
        this.propertyDescriptor = propertyDescriptor;
        this.propertyValue = propertyValue;
        this.requestedType = requestedType;
    }

    private static String getMsg(Type exceptionType, String propertyName,
            PropertyDescriptor propertyDescriptor, Object propertyValue,
            PropertyDescriptor.Type requestedType) {
        if (exceptionType == Type.WrongMultiplicity) {
            return "Property '" + propertyName + "' is not a list";
        } else if (exceptionType == Type.NotACollection) {
            return "Property '" + propertyName + "' is not a collection";
        } else if (exceptionType == Type.NotExist) {
            return "Property '" + propertyName + "' does not exist";
        } else if (exceptionType == Type.TypeMismatch) {
            return "Trying to read " + propertyDescriptor.type().name() + " property '"
                    + propertyName + "' as " + requestedType.name();
        } else if (exceptionType == Type.WrongValueType) {
            return "The value of property '" + propertyName + "' does not have the required type "
                    + propertyDescriptor.type().name() + " " + propertyDescriptor.multiplicity() + ": " + propertyValue;
        } else {
            return null;
        }
    }

    @Getter final Type exceptionType;
    @Getter final String propertyName;
    @Getter final PropertyDescriptor propertyDescriptor;
    @Getter final Object propertyValue;
    @Getter final PropertyDescriptor.Type requestedType;

    public enum Type {
        NotExist, TypeMismatch, WrongValueType, NotACollection, WrongMultiplicity
    }

}

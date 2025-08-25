package cli_tools.common.property_lib;

import lombok.Getter;

@Getter
public class PropertyException extends Exception {

    final Type exceptionType;
    final String propertyName;
    final PropertyDescriptor propertyDescriptor;
    final Object propertyValue;
    final PropertyDescriptor.Type requestedType;
    final String requestedSubtype;
    public PropertyException(Type exceptionType, String propertyName,
                             PropertyDescriptor propertyDescriptor, Object propertyValue,
                             PropertyDescriptor.Type requestedType, String requestedSubtype) {
        super(getMsg(exceptionType, propertyName, propertyDescriptor, propertyValue,
                requestedType, requestedSubtype));

        this.exceptionType = exceptionType;
        this.propertyName = propertyName;
        this.propertyDescriptor = propertyDescriptor;
        this.propertyValue = propertyValue;
        this.requestedType = requestedType;
        this.requestedSubtype = requestedSubtype;
    }

    public PropertyException(String msg) {
        super(msg);
        this.exceptionType = null;
        this.propertyName = null;
        this.propertyDescriptor = null;
        this.propertyValue = null;
        this.requestedType = null;
        this.requestedSubtype = null;
    }

    private static String getMsg(Type exceptionType, String propertyName,
                                 PropertyDescriptor propertyDescriptor, Object propertyValue,
                                 PropertyDescriptor.Type requestedType, String requestedSubtype) {
        if (exceptionType == Type.WrongMultiplicity) {
            return "Property '" + propertyName + "' is not a list";
        } else if (exceptionType == Type.NotACollection) {
            return "Property '" + propertyName + "' is not a collection";
        } else if (exceptionType == Type.NotExist) {
            return "Property '" + propertyName + "' does not exist";
        } else if (exceptionType == Type.TypeMismatch) {
            return "Trying to read " + propertyDescriptor.type().name() + " property '"
                    + propertyName + "' as " + requestedType.name();
        } else if (exceptionType == Type.SubtypeMismatch) {
            return "Trying to read " + propertyDescriptor.subtype().name() + " property '"
                    + propertyName + "' as " + requestedSubtype;
        } else if (exceptionType == Type.WrongValueType) {
            return "The value of property '" + propertyName + "' does not have the required type "
                    + propertyDescriptor.type().name() + " " + propertyDescriptor.multiplicity() + ": " + propertyValue;
        } else if (exceptionType == Type.MultipleMatches) {
            return "Multiple matching properties found for '" + propertyName + "'";
        } else {
            return null;
        }
    }

    public enum Type {
        NotExist, TypeMismatch, WrongValueType, NotACollection, WrongMultiplicity, MultipleMatches, SubtypeMismatch
    }

}

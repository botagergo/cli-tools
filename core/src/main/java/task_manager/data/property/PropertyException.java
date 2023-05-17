package task_manager.data.property;

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

    public Type getExceptionType() {
        return exceptionType;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    private static String getMsg(Type exceptionType, String propertyName,
            PropertyDescriptor propertyDescriptor, Object propertyValue,
            PropertyDescriptor.Type requestedType) {
        if (exceptionType == Type.NotAList) {
            return "Property '" + propertyName + "' is not a list";
        } else if (exceptionType == Type.IsAList) {
            return "Property '" + propertyName + "' is a list";
        } else if (exceptionType == Type.NotExist) {
            return "Property '" + propertyName + "' does not exist";
        } else if (exceptionType == Type.TypeMismatch) {
            return "Trying to read " + propertyDescriptor.type().name() + " property '"
                    + propertyName + "' as " + requestedType.name();
        } else if (exceptionType == Type.WrongValueType) {
            return "The value of property '" + propertyName + "' does not have the required type "
                    + propertyDescriptor.type().name() + ": " + propertyValue;
        } else {
            return null;
        }
    }

    final Type exceptionType;
    final String propertyName;
    final PropertyDescriptor propertyDescriptor;
    final Object propertyValue;
    final PropertyDescriptor.Type requestedType;

    public enum Type {
        NotExist, TypeMismatch, WrongValueType, NotAList, IsAList
    }
}

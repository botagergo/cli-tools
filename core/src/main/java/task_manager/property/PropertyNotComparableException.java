package task_manager.property;

public class PropertyNotComparableException extends Exception {

    public PropertyNotComparableException(PropertyDescriptor propertyDescriptor) {
        super("Property type '" + propertyDescriptor.type() + " " + propertyDescriptor.multiplicity() + "' is not comparable");
    }

}

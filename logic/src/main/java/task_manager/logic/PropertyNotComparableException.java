package task_manager.logic;

import task_manager.core.property.PropertyDescriptor;

public class PropertyNotComparableException extends Exception {

    public PropertyNotComparableException(PropertyDescriptor propertyDescriptor) {
        super("Property type '" + propertyDescriptor.type() + " " + propertyDescriptor.multiplicity() + "' is not comparable");
    }

}

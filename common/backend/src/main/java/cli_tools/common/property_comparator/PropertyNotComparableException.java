package cli_tools.common.property_comparator;

import cli_tools.common.property_lib.PropertyDescriptor;

public class PropertyNotComparableException extends Exception {

    public PropertyNotComparableException(PropertyDescriptor propertyDescriptor) {
        super("Property type '" + propertyDescriptor.type() + " " + propertyDescriptor.multiplicity() + "' is not comparable");
    }

}

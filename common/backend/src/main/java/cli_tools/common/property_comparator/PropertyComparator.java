package cli_tools.common.property_comparator;

import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class PropertyComparator implements Comparator<Property> {

    private final int nullFirstInt;

    public PropertyComparator(boolean nullFirst) {
        this.nullFirstInt = nullFirst ? -1 : 1;
    }

    public PropertyComparator() {
        this.nullFirstInt = -1;
    }

    public static boolean isComparable(PropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE
                && (propertyDescriptor.type() == PropertyDescriptor.Type.String
                || propertyDescriptor.type() == PropertyDescriptor.Type.Boolean
                || propertyDescriptor.type() == PropertyDescriptor.Type.Integer);
    }

    @Override
    public int compare(Property property1, Property property2) {
        if (property1.getPropertyDescriptor().multiplicity() != PropertyDescriptor.Multiplicity.SINGLE ||
                property2.getPropertyDescriptor().multiplicity() != PropertyDescriptor.Multiplicity.SINGLE
        ) {
            throw new RuntimeException();
        }

        if (property1.getValue() == null) {
            if (property2.getValue() == null) {
                return 0;
            } else {
                return nullFirstInt;
            }
        } else if (property2.getValue() == null) {
            return -nullFirstInt;
        } else if (property1.getPropertyDescriptor().type() == PropertyDescriptor.Type.String &&
                property2.getPropertyDescriptor().type() == PropertyDescriptor.Type.String) {
            return StringUtils.compare(property1.getStringUnchecked(), property2.getStringUnchecked());
        } else if (property1.getPropertyDescriptor().type() == PropertyDescriptor.Type.Boolean &&
                property2.getPropertyDescriptor().type() == PropertyDescriptor.Type.Boolean) {
            return Boolean.compare(property1.getBooleanUnchecked(), property2.getBooleanUnchecked());
        } else if (property1.getPropertyDescriptor().type() == PropertyDescriptor.Type.Integer &&
                property2.getPropertyDescriptor().type() == PropertyDescriptor.Type.Integer) {
            return Integer.compare(property1.getIntegerUnchecked(), property2.getIntegerUnchecked());
        } else {
            throw new RuntimeException();
        }
    }

}

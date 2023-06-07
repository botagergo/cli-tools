package task_manager.property;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class PropertyComparator implements Comparator<Property> {

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
                return -1;
            }
        } else if (property2.getValue() == null) {
            return 1;
        } else if (property1.getPropertyDescriptor().type() == PropertyDescriptor.Type.String &&
                property2.getPropertyDescriptor().type() == PropertyDescriptor.Type.String) {
            return StringUtils.compare(property1.getStringUnchecked(), property2.getStringUnchecked());
        } else if (property1.getPropertyDescriptor().type() == PropertyDescriptor.Type.Boolean &&
                property2.getPropertyDescriptor().type() == PropertyDescriptor.Type.Boolean) {
            return Boolean.compare(property1.getBooleanUnchecked(), property2.getBooleanUnchecked());
        } else {
            throw new RuntimeException();
        }
    }

    public static boolean isComparable(PropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE
                    && (propertyDescriptor.type() == PropertyDescriptor.Type.String
                     || propertyDescriptor.type() == PropertyDescriptor.Type.Boolean);
        }

}

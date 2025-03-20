package cli_tools.common.filter;

import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;

import java.util.Collection;

public class EmptyFilterCriterion extends PropertyFilterCriterion {

    public EmptyFilterCriterion(
            String propertyName) {
        super(propertyName);
    }

    @Override
    public boolean check_(Property property) {
        PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
        if (propertyDescriptor.isCollection()) {
            Collection<?> collection = property.getCollectionUnchecked();
            return collection == null || collection.isEmpty();
        } else if (propertyDescriptor.type() == PropertyDescriptor.Type.String) {
            String str = property.getStringUnchecked();
            return str == null || str.isEmpty();
        } else {
            return false;
        }
    }

}

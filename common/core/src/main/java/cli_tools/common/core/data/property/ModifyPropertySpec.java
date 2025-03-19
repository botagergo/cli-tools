package cli_tools.common.core.data.property;

import lombok.NonNull;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyDescriptor;

public record ModifyPropertySpec(
        @NonNull PropertyDescriptor propertyDescriptor,
        Property property,
        @NonNull ModificationType modificationType,
        Option option
) {

    public enum ModificationType {
        SET_VALUE,
        ADD_VALUES,
        REMOVE_VALUES
    }

    public enum Option {
        REMOVE
    }

}
package task_manager.core.property;

import lombok.NonNull;

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
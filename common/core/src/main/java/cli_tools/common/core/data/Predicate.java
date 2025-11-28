package cli_tools.common.core.data;

import cli_tools.common.property_lib.PropertyDescriptor;

public enum Predicate {
    EQUALS,
    CONTAINS,
    IN,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,
    TRUE,
    FALSE,
    FALSE_OR_NULL,
    NULL,
    EMPTY;

    public boolean isCompatibleWithProperty(PropertyDescriptor propertyDescriptor) {
        switch (this) {
            case CONTAINS -> {
                return propertyDescriptor.multiplicity() != PropertyDescriptor.Multiplicity.SINGLE ||
                        propertyDescriptor.type() == PropertyDescriptor.Type.String;
            }
            case IN -> {
                return propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE;
            }
            case LESS, LESS_EQUAL, GREATER, GREATER_EQUAL -> {
                return propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE &&
                        (propertyDescriptor.type() == PropertyDescriptor.Type.Integer ||
                                propertyDescriptor.type() == PropertyDescriptor.Type.String);
            }
            case TRUE, FALSE, FALSE_OR_NULL -> {
                return propertyDescriptor.type() == PropertyDescriptor.Type.Boolean &&
                        propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE;
            }
            default -> {
                return true;
            }
        }
    }
}

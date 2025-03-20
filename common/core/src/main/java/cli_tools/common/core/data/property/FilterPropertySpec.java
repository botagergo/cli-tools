package cli_tools.common.core.data.property;

import cli_tools.common.core.data.Predicate;
import lombok.NonNull;
import cli_tools.common.property_lib.Property;

public record FilterPropertySpec(
        String propertyName,
        Property property,
        boolean negate,
        Predicate predicate) {

}
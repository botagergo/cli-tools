package cli_tools.common.core.data.property;

import cli_tools.common.core.data.Predicate;
import cli_tools.common.property_lib.PropertyDescriptor;

import java.util.List;

public record FilterPropertySpec(
        PropertyDescriptor propertyDescriptor,
        List<Object> operand,
        boolean negate,
        Predicate predicate) {

}
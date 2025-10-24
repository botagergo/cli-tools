package cli_tools.common.backend.filter;

import cli_tools.common.property_lib.Property;
import lombok.Getter;

@Getter
public class EqualFilterCriterion extends PropertyFilterCriterion {

    private final Object operand;

    public EqualFilterCriterion(
            String propertyName,
            Object operand) {
        super(propertyName);
        this.operand = operand;
    }

    @Override
    public boolean check_(Property property) {
        if (property.getValue() == null) {
            return operand == null;
        } else {
            return property.getValue().equals(operand);
        }
    }

}

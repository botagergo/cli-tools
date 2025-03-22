package cli_tools.common.filter;

import lombok.Getter;
import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyException;

@Getter
public class ContainsCaseInsensitiveFilterCriterion extends PropertyFilterCriterion {

    public ContainsCaseInsensitiveFilterCriterion(
            String propertyName,
            String operand) {
        super(propertyName);
        this.operand = operand;
    }

    @Override
    public boolean check_(Property property) throws PropertyException {
        if (operand == null) {
            return false;
        }
        String propertyValueStr = property.getString();
        return propertyValueStr.toLowerCase().contains(operand.toLowerCase());
    }

    private final String operand;

}

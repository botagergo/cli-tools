package cli_tools.common.filter;

import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyException;

import java.util.Collection;

public class InFilterCriterion extends PropertyFilterCriterion {

    private final Collection<Object> operand;

    public InFilterCriterion(String propertyName, Collection<Object> operand) {
        super(propertyName);
        this.operand = operand;
    }

    @Override
    public boolean check_(Property property) throws PropertyException {
        return operand != null && operand.contains(property.getValue());
    }

}

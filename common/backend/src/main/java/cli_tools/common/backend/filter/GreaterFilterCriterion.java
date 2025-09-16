package cli_tools.common.backend.filter;

import cli_tools.common.property_lib.Property;

import java.util.Comparator;

public class GreaterFilterCriterion extends PropertyFilterCriterion {

    private final Property operand;
    private final Comparator<Property> propertyComparator;

    public GreaterFilterCriterion(
            String propertyName,
            Property operand,
            Comparator<Property> propertyComparator
    ) {
        super(propertyName);
        this.operand = operand;
        this.propertyComparator = propertyComparator;
    }

    @Override
    public boolean check_(Property property) {
        return propertyComparator.compare(property, operand) > 0;
    }

}

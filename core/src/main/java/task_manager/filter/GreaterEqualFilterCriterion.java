package task_manager.filter;

import task_manager.property.Property;
import task_manager.property.PropertyException;

import java.util.Comparator;

public class GreaterEqualFilterCriterion extends PropertyFilterCriterion {

    private final Property operand;
    private final Comparator<Property> propertyComparator;

    public GreaterEqualFilterCriterion(
            String propertyName,
            Property operand,
            Comparator<Property> propertyComparator
    ) {
        super(propertyName);
        this.operand = operand;
        this.propertyComparator = propertyComparator;
    }

    @Override
    public boolean check_(Property property) throws PropertyException {
        return propertyComparator.compare(property, operand) >= 0;
    }

}

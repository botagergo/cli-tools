package task_manager.logic.filter;

import task_manager.core.property.Property;
import task_manager.core.property.PropertyException;

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
    public boolean check_(Property property) throws PropertyException {
        return propertyComparator.compare(property, operand) > 0;
    }

}

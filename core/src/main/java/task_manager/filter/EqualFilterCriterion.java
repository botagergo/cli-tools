package task_manager.filter;

import task_manager.property.Property;

public class EqualFilterCriterion extends PropertyFilterCriterion {

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

    private final Object operand;

}

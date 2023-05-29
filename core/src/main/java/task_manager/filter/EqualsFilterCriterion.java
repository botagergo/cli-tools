package task_manager.filter;

import task_manager.property.Property;

public class EqualsFilterCriterion extends PropertyFilterCriterion {

    public EqualsFilterCriterion(String propertyName, Object operand) {
        super(propertyName);
        this.operand = operand;
    }

    @Override
    public boolean check_(Property property) {
        if (property.getRawValue() == null) {
            return operand == null;
        } else {
            return property.getRawValue().equals(operand);
        }
    }

    private final Object operand;

}

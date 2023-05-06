package task_manager.api.filter;

import task_manager.db.property.Property;
import task_manager.db.property.PropertyException;

public class EqualsFilterCriterion extends PropertyFilterCriterion {

    public EqualsFilterCriterion(String propertyName, Object operand) {
        super(propertyName);
        this.operand = operand;
    }

    @Override
    public boolean check_(Property property) throws PropertyException {
        if (property.getValue() == null) {
            return operand == null;
        } else {
            return property.getValue().equals(operand);
        }
    }

    private Object operand;

}

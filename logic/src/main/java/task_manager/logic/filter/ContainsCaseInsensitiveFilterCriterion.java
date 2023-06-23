package task_manager.logic.filter;

import lombok.Getter;
import task_manager.core.property.Property;
import task_manager.core.property.PropertyException;

public class ContainsCaseInsensitiveFilterCriterion extends PropertyFilterCriterion {

    public ContainsCaseInsensitiveFilterCriterion(
            String propertyName,
            String operand) {
        super(propertyName);
        this.operand = operand;
    }

    @Override
    public boolean check_(Property property) throws PropertyException {
        String propertyValueStr = property.getString();
        return propertyValueStr.toLowerCase().contains(operand.toLowerCase());
    }

    @Getter
    private final String operand;

}

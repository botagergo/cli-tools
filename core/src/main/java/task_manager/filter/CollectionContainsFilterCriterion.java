package task_manager.filter;

import task_manager.property.Property;
import task_manager.property.PropertyException;

import java.util.Collection;

public class CollectionContainsFilterCriterion extends PropertyFilterCriterion {

    public CollectionContainsFilterCriterion(String propertyName, Collection<Object> operand) {
        super(propertyName);
        this.operand = operand;
    }

    @Override
    public boolean check_(Property property) throws PropertyException {
        return property.getCollection().containsAll(operand);
    }

    private final Collection<Object> operand;

}

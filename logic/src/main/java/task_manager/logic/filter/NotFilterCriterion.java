package task_manager.logic.filter;

import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertyManager;
import task_manager.core.property.PropertyOwner;

import java.io.IOException;

public class NotFilterCriterion extends FilterCriterion {

    public NotFilterCriterion(FilterCriterion filterCriterion) {
        this.filterCriterion = filterCriterion;
    }

    @Override
    protected boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        return !filterCriterion.check_(propertyOwner, propertyManager);
    }

    private final FilterCriterion filterCriterion;
}

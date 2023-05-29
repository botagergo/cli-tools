package task_manager.filter;

import java.io.IOException;
import java.util.List;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyOwner;

public class OrFilterCriterion extends FilterCriterion {
    public OrFilterCriterion(FilterCriterion... criteria) {
        this.criterions = List.of(criteria);
    }

    final List<FilterCriterion> criterions;

    @Override
    protected boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        for (FilterCriterion criterion : criterions) {
            if (criterion.check(propertyOwner, propertyManager)) {
                return true;
            }
        }
        return false;
    }
}

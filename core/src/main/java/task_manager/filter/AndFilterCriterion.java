package task_manager.filter;

import java.io.IOException;
import java.util.List;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyOwner;

public class AndFilterCriterion extends FilterCriterion {

    public AndFilterCriterion(List<FilterCriterion> criteria) {
        this.criteria = criteria;
    }

    public AndFilterCriterion(FilterCriterion... criteria) {
        this(List.of(criteria));
    }

    final List<FilterCriterion> criteria;

    @Override
    public boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        for (FilterCriterion criterion : criteria) {
            if (!criterion.check(propertyOwner, propertyManager)) {
                return false;
            }
        }
        return true;
    }
}

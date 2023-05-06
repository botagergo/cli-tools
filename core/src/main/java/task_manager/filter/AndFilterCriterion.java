package task_manager.filter;

import java.util.List;
import task_manager.data.property.PropertyException;
import task_manager.data.property.PropertyOwner;

public class AndFilterCriterion extends FilterCriterion {

    public AndFilterCriterion(List<FilterCriterion> criteria) {
        this.criteria = criteria;
    }

    public AndFilterCriterion(FilterCriterion... criteria) {
        this(List.of(criteria));
    }

    final List<FilterCriterion> criteria;

    @Override
    public boolean check_(PropertyOwner propertyOwner) throws PropertyException {
        for (FilterCriterion criterion : criteria) {
            if (!criterion.check(propertyOwner)) {
                return false;
            }
        }
        return true;
    }
}

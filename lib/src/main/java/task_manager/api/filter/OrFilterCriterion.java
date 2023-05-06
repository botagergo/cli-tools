package task_manager.api.filter;

import java.util.List;
import task_manager.db.property.PropertyException;
import task_manager.db.property.PropertyOwner;

public class OrFilterCriterion extends FilterCriterion {
    public OrFilterCriterion(FilterCriterion... criterions) {
        this.criterions = List.of(criterions);
    }

    List<FilterCriterion> criterions;

    @Override
    public boolean check_(PropertyOwner propertyOwner) throws PropertyException {
        for (FilterCriterion criterion : criterions) {
            if (criterion.check(propertyOwner)) {
                return true;
            }
        }
        return false;
    }
}

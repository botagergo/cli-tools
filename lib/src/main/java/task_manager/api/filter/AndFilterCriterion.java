package task_manager.api.filter;

import java.util.List;
import task_manager.db.property.PropertyException;
import task_manager.db.property.PropertyOwner;

public class AndFilterCriterion extends FilterCriterion {

    public AndFilterCriterion(List<FilterCriterion> criterions) {
        this.criterions = criterions;
    }

    public AndFilterCriterion(FilterCriterion... criterions) {
        this(List.of(criterions));
    }

    List<FilterCriterion> criterions;

    @Override
    public boolean check_(PropertyOwner propertyOwner) throws PropertyException {
        for (FilterCriterion criterion : criterions) {
            if (!criterion.check(propertyOwner)) {
                return false;
            }
        }
        return true;
    }
}

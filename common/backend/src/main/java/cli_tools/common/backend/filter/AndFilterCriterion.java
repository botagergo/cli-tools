package cli_tools.common.backend.filter;

import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class AndFilterCriterion extends FilterCriterion {

    private final Collection<FilterCriterion> criteria;

    public AndFilterCriterion(Collection<FilterCriterion> criteria) {
        this.criteria = criteria;
    }

    public AndFilterCriterion(FilterCriterion... criteria) {
        this(List.of(criteria));
    }

    @Override
    public boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException {
        for (FilterCriterion criterion : criteria) {
            if (!criterion.check(propertyOwner, propertyManager)) {
                return false;
            }
        }
        return true;
    }

}

package cli_tools.common.filter;

import lombok.Getter;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Getter
public class AndFilterCriterion extends FilterCriterion {

    public AndFilterCriterion(Collection<FilterCriterion> criteria) {
        this.criteria = criteria;
    }

    public AndFilterCriterion(FilterCriterion... criteria) {
        this(List.of(criteria));
    }

    @Override
    public boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        for (FilterCriterion criterion : criteria) {
            if (!criterion.check(propertyOwner, propertyManager)) {
                return false;
            }
        }
        return true;
    }

    private final Collection<FilterCriterion> criteria;

}

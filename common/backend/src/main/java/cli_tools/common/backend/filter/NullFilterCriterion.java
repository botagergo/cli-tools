package cli_tools.common.backend.filter;

import cli_tools.common.property_lib.Property;

public class NullFilterCriterion extends PropertyFilterCriterion {

    public NullFilterCriterion(
            String propertyName) {
        super(propertyName);
    }

    @Override
    public boolean check_(Property property) {
        return property.getValue() == null;
    }

}

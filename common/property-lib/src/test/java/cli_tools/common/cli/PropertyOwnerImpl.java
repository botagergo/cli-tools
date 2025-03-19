package cli_tools.common.cli;

import cli_tools.common.property_lib.PropertyOwner;

import java.util.HashMap;

public class PropertyOwnerImpl extends PropertyOwner {

    public PropertyOwnerImpl(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public PropertyOwnerImpl() {
        this(new HashMap<>());
    }

    @Override
    public HashMap<String, Object> getProperties() {
        return properties;
    }

    private final HashMap<String, Object> properties;

}

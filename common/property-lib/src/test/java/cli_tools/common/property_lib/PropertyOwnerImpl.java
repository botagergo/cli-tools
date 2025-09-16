package cli_tools.common.property_lib;

import java.util.HashMap;

public class PropertyOwnerImpl extends PropertyOwner {

    private final HashMap<String, Object> properties;

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

}

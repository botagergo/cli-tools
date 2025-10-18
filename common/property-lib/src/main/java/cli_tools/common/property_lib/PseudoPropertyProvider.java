package cli_tools.common.property_lib;

public interface PseudoPropertyProvider {
    Object getProperty(PropertyManager propertyManager, PropertyOwner propertyOwner) throws PropertyException;
}

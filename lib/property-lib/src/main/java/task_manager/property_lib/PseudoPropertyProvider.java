package task_manager.property_lib;

import java.io.IOException;

public interface PseudoPropertyProvider {
    Object getProperty(PropertyManager propertyManager, PropertyOwner propertyOwner) throws IOException, PropertyException;
}

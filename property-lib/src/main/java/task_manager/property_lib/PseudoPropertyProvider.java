package task_manager.property_lib;

import java.io.IOException;

public interface PseudoPropertyProvider {
    Object getProperty(PropertyOwner propertyOwner) throws IOException;
}

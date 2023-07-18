package task_manager.core.property;

import java.io.IOException;

public interface PseudoPropertyProvider {
    Object getProperty(PropertyOwner propertyOwner) throws IOException;
}

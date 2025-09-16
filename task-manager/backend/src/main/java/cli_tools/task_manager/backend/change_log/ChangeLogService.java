package cli_tools.task_manager.backend.change_log;

import cli_tools.common.property_lib.PropertyOwner;

public interface ChangeLogService {
    void addAdded(PropertyOwner propertyOwner);

    void addModified(PropertyOwner origPropertyOwner, PropertyOwner newPropertyOwner);

    void addDeleted(PropertyOwner propertyOwner);
}

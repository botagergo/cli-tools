package cli_tools.task_manager.backend.change_log;

import cli_tools.common.property_lib.PropertyOwner;

import java.util.UUID;

public interface ChangeLogRepository {
    void addAdded(UUID uuid, PropertyOwner propertyOwner);

    void addModified(UUID uuid, PropertyOwner origPropertyOwner, PropertyOwner newPropertyOwner);

    void addDeleted(UUID uuid, PropertyOwner propertyOwner);
}

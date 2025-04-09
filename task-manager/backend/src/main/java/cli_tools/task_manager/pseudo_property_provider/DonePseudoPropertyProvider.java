package cli_tools.task_manager.pseudo_property_provider;

import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.common.property_lib.PseudoPropertyProvider;
import cli_tools.task_manager.task.Task;

public class DonePseudoPropertyProvider implements PseudoPropertyProvider {
    @Override
    public Boolean getProperty(PropertyManager propertyManager, PropertyOwner propertyOwner) {
        if (propertyOwner instanceof Task task) {
            return task.isDone();
        } else {
            return null;
        }
    }
}

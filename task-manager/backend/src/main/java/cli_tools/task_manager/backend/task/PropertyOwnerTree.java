package cli_tools.task_manager.backend.task;

import cli_tools.common.property_lib.PropertyOwner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PropertyOwnerTree extends PropertyOwner {

    private PropertyOwner parent;
    private List<PropertyOwnerTree> children;

    @Override
    public HashMap<String, Object> getProperties() {
        return parent.getProperties();
    }
}
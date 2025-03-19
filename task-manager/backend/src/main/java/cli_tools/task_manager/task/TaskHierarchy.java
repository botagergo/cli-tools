package cli_tools.task_manager.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cli_tools.common.property_lib.PropertyOwner;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TaskHierarchy extends PropertyOwner {

    @Override
    public HashMap<String, Object> getProperties() {
        return parent.getProperties();
    }

    private Task parent;
    private List<TaskHierarchy> children;
}
package task_manager.core.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import task_manager.property_lib.PropertyOwner;

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
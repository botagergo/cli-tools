package task_manager;

import lombok.AllArgsConstructor;
import task_manager.property.PropertyOwner;

import java.util.HashMap;

@AllArgsConstructor
public class PropertyOwnerImpl implements PropertyOwner {

    public PropertyOwnerImpl() {
        this.properties = new HashMap<>();
    }

    @Override
    public HashMap<String, Object> getProperties() {
        return properties;
    }

    private HashMap<String, Object> properties;

}

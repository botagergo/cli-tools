package task_manager.property_lib;

import java.util.HashMap;
import java.util.UUID;

public abstract class PropertyOwner {

    public abstract HashMap<String, Object> getProperties();

    public UUID getUUID() {
        Object uuid = getProperties().get("uuid");
        if (!(uuid instanceof UUID)) {
            return null;
        }
        return (UUID) uuid;
    }

}

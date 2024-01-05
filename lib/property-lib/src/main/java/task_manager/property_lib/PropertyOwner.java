package task_manager.property_lib;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.UUID;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE
)
public abstract class PropertyOwner {

    @JsonProperty
    public abstract HashMap<String, Object> getProperties();

    public UUID getUUID() {
        Object uuid = getProperties().get("uuid");
        if (!(uuid instanceof UUID)) {
            return null;
        }
        return (UUID) uuid;
    }

}

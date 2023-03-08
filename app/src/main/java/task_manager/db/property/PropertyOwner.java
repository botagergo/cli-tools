package task_manager.db.property;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class PropertyOwner {
    public abstract PropertyManager getPropertyManager();

    public abstract Map<String, Object> getProperties();

    public Object getProperty(String propertyName) throws PropertyException {
        return getPropertyManager().getProperty(this, propertyName);
    }

    public void setProperty(String propertyName, Object propertyValue) throws PropertyException {
        getPropertyManager().setProperty(this, propertyName, propertyValue);
    }

    public String getStringProperty(String propertyName) throws PropertyException {
        return getPropertyManager().getStringProperty(this, propertyName);
    }

    public Boolean getBooleanProperty(String propertyName) throws PropertyException {
        return getPropertyManager().getBooleanProperty(this, propertyName);
    }

    public UUID getUuidProperty(String propertyName) throws PropertyException {
        return getPropertyManager().getUuidProperty(this, propertyName);
    }

    public List<UUID> getUuidListProperty(String propertyName) throws PropertyException {
        return getPropertyManager().getUuidListProperty(this, propertyName);
    }
}

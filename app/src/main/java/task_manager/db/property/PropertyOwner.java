package task_manager.db.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;

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

    public Iterable<Pair<String, Object>> getPropertiesIter() throws PropertyException {
        List<Pair<String, Object>> properties = new ArrayList<>();
        for (String propertyName : getProperties().keySet()) {
            properties.add(Pair.of(propertyName, getProperty(propertyName)));
        }
        return properties;
    }
}

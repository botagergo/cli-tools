package task_manager.db.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;

public abstract class PropertyOwner {
    public abstract PropertyManager getPropertyManager();

    public abstract HashMap<String, Object> getRawProperties();

    public Property getProperty(String propertyName) throws PropertyException {
        return getPropertyManager().getProperty(this, propertyName);
    }

    public PropertyOwner setProperty(String propertyName, Object propertyValue)
        throws PropertyException {
        getPropertyManager().setProperty(this, propertyName, propertyValue);
        return this;
    }

    public String getStringProperty(String propertyName) throws PropertyException {
        return getPropertyManager().getProperty(this, propertyName).getString();
    }

    public Boolean getBooleanProperty(String propertyName) throws PropertyException {
        return getPropertyManager().getProperty(this, propertyName).getBoolean();
    }

    public UUID getUuidProperty(String propertyName) throws PropertyException {
        return getPropertyManager().getProperty(this, propertyName).getUuid();
    }

    public List<UUID> getUuidListProperty(String propertyName) throws PropertyException {
        return getPropertyManager().getProperty(this, propertyName).getUuidList();
    }

    public Iterable<Pair<String, Property>> getPropertiesIter() throws PropertyException {
        List<Pair<String, Property>> properties = new ArrayList<>();
        for (String propertyName : getRawProperties().keySet()) {
            properties.add(Pair.of(propertyName, getProperty(propertyName)));
        }
        return properties;
    }
}

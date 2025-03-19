package task_manager.logic.pseudo_property_provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import task_manager.property_lib.*;

import java.io.IOException;

public class AliasPseudoPropertyProvider implements PseudoPropertyProvider {
    public AliasPseudoPropertyProvider(@JsonProperty("propertyName") String propertyName) {
        this.propertyName = propertyName;
    }
    @Override
    public Object getProperty(PropertyManager propertyManager, PropertyOwner propertyOwner) throws IOException, PropertyException {
        return propertyManager.getProperty(propertyOwner, propertyName).getValue();
    }
    private final String propertyName;
}

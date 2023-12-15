package task_manager.property_lib;

import java.io.IOException;
import java.util.HashMap;

public class PseudoPropertyManager {

    public PseudoPropertyManager() {
        this.pseudoPropertyProviders = new HashMap<>();
    }

    public Property getPseudoProperty(PropertyOwner propertyOwner, PropertyDescriptor propertyDescriptor) throws IOException, PropertyException {
        PseudoPropertyProvider pseudoPropertyProvider = pseudoPropertyProviders.get(propertyDescriptor.name());
        if (pseudoPropertyProvider == null) {
            return null;
        }

        Object propertyValue = pseudoPropertyProvider.getProperty(propertyOwner);
        return Property.from(propertyDescriptor, propertyValue);
    }

    public void registerPseudoPropertyProvider(String propertyName, PseudoPropertyProvider pseudoPropertyProvider) {
        pseudoPropertyProviders.put(propertyName, pseudoPropertyProvider);
    }

    private final HashMap<String, PseudoPropertyProvider> pseudoPropertyProviders;

}

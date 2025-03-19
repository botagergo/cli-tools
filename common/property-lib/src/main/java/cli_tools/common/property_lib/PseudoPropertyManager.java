package cli_tools.common.property_lib;

import java.io.IOException;
import java.util.HashMap;

public class PseudoPropertyManager {

    public PseudoPropertyManager(PropertyManager propertyManager) {
        this.pseudoPropertyProviders = new HashMap<>();
        this.propertyManager = propertyManager;
    }

    public Property getPseudoProperty(PropertyOwner propertyOwner, PropertyDescriptor propertyDescriptor) throws IOException, PropertyException {
        PseudoPropertyProvider pseudoPropertyProvider = pseudoPropertyProviders.get(propertyDescriptor.name());
        if (pseudoPropertyProvider == null) {
            return null;
        }

        Object propertyValue = pseudoPropertyProvider.getProperty(propertyManager, propertyOwner);
        return Property.from(propertyDescriptor, propertyValue);
    }

    public void registerPseudoPropertyProvider(String propertyName, PseudoPropertyProvider pseudoPropertyProvider) {
        pseudoPropertyProviders.put(propertyName, pseudoPropertyProvider);
    }

    private final HashMap<String, PseudoPropertyProvider> pseudoPropertyProviders;
    private final PropertyManager propertyManager;

}

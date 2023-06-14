package task_manager.ui.cli.command.property_modifier;

import task_manager.property.*;

import java.io.IOException;
import java.util.List;

public class PropertyModifier {


    public static void modifyProperties(PropertyManager propertyManager, PropertyOwner propertyOwner, List<PropertySpec> propertySpecs) throws PropertyException, IOException, PropertyModifierException {
        for (PropertySpec propertySpec : propertySpecs) {
            PropertyDescriptor propertyDescriptor = propertySpec.property().getPropertyDescriptor();
            Property property = propertySpec.property();

            if ((propertySpec.affinity() == PropertySpec.Affinity.POSITIVE
                    || propertySpec.affinity() == PropertySpec.Affinity.NEGATIVE)
                    && propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE) {
                throw new PropertyException(PropertyException.Type.NotACollection,
                        property.getPropertyDescriptor().name(), property.getPropertyDescriptor(), null,
                        PropertyDescriptor.Type.UUID);
            }

            if (propertySpec.predicate() != null) {
                throw new PropertyModifierException();
            }
        }

        for (PropertySpec propertySpec : propertySpecs) {
            PropertyDescriptor propertyDescriptor = propertySpec.property().getPropertyDescriptor();
            Property property = propertySpec.property();
            if (propertySpec.affinity() == PropertySpec.Affinity.POSITIVE) {
                propertyManager.addProperty(propertyOwner, propertyDescriptor.name(), property.getCollection());
            } else if (propertySpec.affinity() == PropertySpec.Affinity.NEGATIVE) {
                propertyManager.removeProperty(propertyOwner, propertyDescriptor.name(), property.getCollection());
            } else {
                propertyManager.setProperty(propertyOwner, propertyDescriptor.name(), propertySpec.property().getValue());
            }
        }
    }

}

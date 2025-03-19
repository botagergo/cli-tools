package cli_tools.common.cli.property_modifier;

import cli_tools.common.core.data.property.ModifyPropertySpec;
import cli_tools.common.property_lib.*;

import java.io.IOException;
import java.util.List;

public class PropertyModifier {

    public static void modifyProperties(PropertyManager propertyManager, PropertyOwner propertyOwner, List<ModifyPropertySpec> modifyPropertySpecs) throws PropertyException, IOException, PropertyModifierException {
        validateProperties(modifyPropertySpecs);
        doModifyProperties(propertyManager, propertyOwner, modifyPropertySpecs);
    }

    private static void validateProperties(List<ModifyPropertySpec> modifyPropertySpecs) throws PropertyModifierException {
        for (ModifyPropertySpec modifyPropertySpec : modifyPropertySpecs) {
            PropertyDescriptor propertyDescriptor = modifyPropertySpec.propertyDescriptor();

            if ((modifyPropertySpec.modificationType() == ModifyPropertySpec.ModificationType.ADD_VALUES
                    || modifyPropertySpec.modificationType() == ModifyPropertySpec.ModificationType.REMOVE_VALUES)
                    && propertyDescriptor.multiplicity() == PropertyDescriptor.Multiplicity.SINGLE) {
                throw new PropertyModifierException(PropertyModifierException.Type.NotACollection,
                        modifyPropertySpec, "Property '" + modifyPropertySpec.propertyDescriptor().name() + "' is not a collection");
            }

            if (modifyPropertySpec.option() != null) {
                //noinspection SwitchStatementWithTooFewBranches
                switch (modifyPropertySpec.option()) {
                    case REMOVE -> {
                        if (modifyPropertySpec.modificationType() == ModifyPropertySpec.ModificationType.ADD_VALUES) {
                            throw new PropertyModifierException(
                                    PropertyModifierException.Type.BadModificationType,
                                    modifyPropertySpec,
                                    "Cannot use '+' with 'remove' option");
                        } else if (modifyPropertySpec.modificationType() == ModifyPropertySpec.ModificationType.REMOVE_VALUES) {
                            throw new PropertyModifierException(
                                    PropertyModifierException.Type.BadModificationType,
                                    modifyPropertySpec,
                                    "Cannot use '-' with 'remove' option");
                        }
                    }
                }
            }
        }
    }

    private static void doModifyProperties(PropertyManager propertyManager, PropertyOwner propertyOwner, List<ModifyPropertySpec> modifyPropertySpecs) throws PropertyException, IOException {
        for (ModifyPropertySpec modifyPropertySpec : modifyPropertySpecs) {
            PropertyDescriptor propertyDescriptor = modifyPropertySpec.propertyDescriptor();
            Property property = modifyPropertySpec.property();

            if (modifyPropertySpec.option() != null) {
                //noinspection SwitchStatementWithTooFewBranches
                switch (modifyPropertySpec.option()) {
                    case REMOVE -> propertyManager.removeProperty(propertyOwner, propertyDescriptor.name());
                }
            } else if (modifyPropertySpec.modificationType() == ModifyPropertySpec.ModificationType.ADD_VALUES) {
                propertyManager.addPropertyValues(propertyOwner, propertyDescriptor.name(), property.getCollection());
            } else if (modifyPropertySpec.modificationType() == ModifyPropertySpec.ModificationType.REMOVE_VALUES) {
                propertyManager.removePropertyValues(propertyOwner, propertyDescriptor.name(), property.getCollection());
            } else {
                propertyManager.setProperty(propertyOwner, propertyDescriptor.name(), modifyPropertySpec.property().getValue());
            }
        }
    }

}

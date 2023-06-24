package task_manager.init;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.core.data.Label;
import task_manager.core.property.PropertyDescriptor;
import task_manager.logic.use_case.label.LabelUseCase;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCase;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class Initializer {

    public boolean needsInitialization() throws IOException {
        List<PropertyDescriptor> propertyDescriptorCollection = propertyDescriptorUseCase.getPropertyDescriptors();
        return propertyDescriptorCollection.isEmpty();
    }

    public void initialize() throws IOException {
        initializePropertyDescriptors();
        initializeStatuses();
        initializePriorities();
    }

    private void initializeStatuses() throws IOException {
        labelUseCase.createLabel("status", new Label(UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(690234862).array()), "NextAction"));
        labelUseCase.createLabel("status", new Label(UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(495582995).array()), "Waiting"));
        labelUseCase.createLabel("status", new Label(UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(199213821).array()), "Planning"));
        labelUseCase.createLabel("status", new Label(UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(314566632).array()), "OnHold"));
    }

    private void initializePropertyDescriptors() throws IOException {
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("name", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, ""));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("uuid", PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.SINGLE, ""));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("done", PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE, false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("tags", PropertyDescriptor.Type.UUID, new PropertyDescriptor.UUIDExtra("tag"), PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>()));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("status", PropertyDescriptor.Type.UUID, new PropertyDescriptor.UUIDExtra("status"), PropertyDescriptor.Multiplicity.SINGLE, null));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("priority", PropertyDescriptor.Type.Integer, new PropertyDescriptor.IntegerExtra("priority"), PropertyDescriptor.Multiplicity.SINGLE, null));
    }

    private void initializePriorities() throws IOException {
        orderedLabelUseCase.createOrderedLabel("priority", "low");
        orderedLabelUseCase.createOrderedLabel("priority", "medium");
        orderedLabelUseCase.createOrderedLabel("priority", "high");
    }

    private final PropertyDescriptorUseCase propertyDescriptorUseCase;
    private final LabelUseCase labelUseCase;
    private final OrderedLabelUseCase orderedLabelUseCase;

}

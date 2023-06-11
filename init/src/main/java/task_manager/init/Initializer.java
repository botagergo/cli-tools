package task_manager.init;

import jakarta.inject.Inject;
import task_manager.logic.use_case.PropertyDescriptorUseCase;
import task_manager.logic.use_case.StatusUseCase;
import task_manager.property.PropertyDescriptor;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

public class Initializer {

    @Inject
    public Initializer(
            PropertyDescriptorUseCase propertyDescriptorUseCase,
            StatusUseCase statusUseCase) {
        this.propertyDescriptorUseCase = propertyDescriptorUseCase;
        this.statusUseCase = statusUseCase;
    }

    public boolean needsInitialization() throws IOException {
        List<PropertyDescriptor> propertyDescriptorCollection = propertyDescriptorUseCase.getPropertyDescriptors();
        return propertyDescriptorCollection.isEmpty();
    }

    public void initialize() throws IOException {
        initializePropertyDescriptors();
        initializeStatuses();
    }

    private void initializePropertyDescriptors() throws IOException {
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("name", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, ""));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("uuid", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, ""));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("done", PropertyDescriptor.Type.Boolean, PropertyDescriptor.Multiplicity.SINGLE, false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("tags", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>()));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("status", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SINGLE, null));
    }

    private void initializeStatuses() throws IOException {
        statusUseCase.createStatus("NextAction");
        statusUseCase.createStatus("Waiting");
        statusUseCase.createStatus("Planning");
        statusUseCase.createStatus("OnHold");
    }

    private final PropertyDescriptorUseCase propertyDescriptorUseCase;
    private final StatusUseCase statusUseCase;

}

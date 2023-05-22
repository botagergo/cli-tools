package task_manager.init;

import jakarta.inject.Inject;
import task_manager.data.Label;
import task_manager.data.property.PropertyDescriptor;
import task_manager.data.property.PropertyDescriptorCollection;
import task_manager.repository.LabelRepository;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.repository.PropertyDescriptorRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Initializer {

    @Inject
    public Initializer(PropertyDescriptorRepository propertyDescriptorRepository, LabelRepositoryFactory labelRepositoryFactory) {
        this.propertyDescriptorRepository = propertyDescriptorRepository;
        this.statusRepository = labelRepositoryFactory.getLabelRepository("status");
    }

    public boolean needsInitialization() throws IOException {
        PropertyDescriptorCollection propertyDescriptorCollection = propertyDescriptorRepository.getAll();
        return propertyDescriptorCollection.isEmpty();
    }

    public void initialize() throws IOException {
        initializePropertyDescriptors();
        initializeStatuses();
    }

    private void initializePropertyDescriptors() throws IOException {
        propertyDescriptorRepository.create(
                new PropertyDescriptor("name", PropertyDescriptor.Type.String, false, ""));
        propertyDescriptorRepository.create(
                new PropertyDescriptor("uuid", PropertyDescriptor.Type.UUID, false, ""));
        propertyDescriptorRepository.create(
                new PropertyDescriptor("done", PropertyDescriptor.Type.Boolean, false, false));
        propertyDescriptorRepository.create(
                new PropertyDescriptor("tags", PropertyDescriptor.Type.UUID, true, List.of()));
        propertyDescriptorRepository.create(
                new PropertyDescriptor("status", PropertyDescriptor.Type.UUID, false, null));
    }

    private void initializeStatuses() throws IOException {
        statusRepository.create(new Label(UUID.randomUUID(), "NextAction"));
        statusRepository.create(new Label(UUID.randomUUID(), "Waiting"));
        statusRepository.create(new Label(UUID.randomUUID(), "Planning"));
        statusRepository.create(new Label(UUID.randomUUID(), "OnHold"));
    }

    private final PropertyDescriptorRepository propertyDescriptorRepository;
    private final LabelRepository statusRepository;

}

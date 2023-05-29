package task_manager.init;

import jakarta.inject.Inject;
import task_manager.data.Label;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyDescriptorCollection;
import task_manager.repository.LabelRepository;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.repository.PropertyDescriptorRepository;
import task_manager.util.UUIDGenerator;

import java.io.IOException;
import java.util.List;

public class Initializer {

    @Inject
    public Initializer(
            PropertyDescriptorRepository propertyDescriptorRepository,
            LabelRepositoryFactory labelRepositoryFactory,
            UUIDGenerator uuidGenerator) {
        this.propertyDescriptorRepository = propertyDescriptorRepository;
        this.statusRepository = labelRepositoryFactory.getLabelRepository("statuses");
        this.uuidGenerator = uuidGenerator;
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
        statusRepository.create(new Label(uuidGenerator.getUUID(), "NextAction"));
        statusRepository.create(new Label(uuidGenerator.getUUID(), "Waiting"));
        statusRepository.create(new Label(uuidGenerator.getUUID(), "Planning"));
        statusRepository.create(new Label(uuidGenerator.getUUID(), "OnHold"));
    }

    private final PropertyDescriptorRepository propertyDescriptorRepository;
    private final LabelRepository statusRepository;
    private final UUIDGenerator uuidGenerator;

}

package task_manager.init;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.yaml.snakeyaml.Yaml;
import task_manager.core.data.*;
import task_manager.core.repository.ConfigurationRepository;
import task_manager.logic.use_case.label.LabelUseCase;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCase;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import task_manager.logic.use_case.view.ViewInfoUseCase;
import task_manager.property_lib.PropertyDescriptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class Initializer {

    @Inject
    public Initializer(
            PropertyDescriptorUseCase propertyDescriptorUseCase,
            LabelUseCase labelUseCase,
            OrderedLabelUseCase orderedLabelUseCase,
            ConfigurationRepository configurationRepository,
            ViewInfoUseCase viewInfoUseCase,
            @Named("configurationYamlFile") File configFile
    ) {
        this.propertyDescriptorUseCase = propertyDescriptorUseCase;
        this.labelUseCase = labelUseCase;
        this.orderedLabelUseCase = orderedLabelUseCase;
        this.viewInfoUseCase = viewInfoUseCase;
        this.configurationRepository = configurationRepository;
        this.configFile = configFile;
    }

    public boolean needsInitialization() throws IOException {
        List<PropertyDescriptor> propertyDescriptorCollection = propertyDescriptorUseCase.getPropertyDescriptors();
        return propertyDescriptorCollection.isEmpty();
    }

    public void initialize() throws IOException {
        initializePropertyDescriptors();
        initializeStatuses();
        initializePriorities();
        initializeEfforts();
        initializeViewInfo();
        initializeConfig();
    }

    private void initializeStatuses() throws IOException {
        labelUseCase.deleteAllLabels("status");
        labelUseCase.createLabel("status", new Label(UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(690234862).array()), "NextAction"));
        labelUseCase.createLabel("status", new Label(UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(495582995).array()), "Waiting"));
        labelUseCase.createLabel("status", new Label(UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(199213821).array()), "Planning"));
        labelUseCase.createLabel("status", new Label(UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(314566632).array()), "OnHold"));
    }

    private void initializePropertyDescriptors() throws IOException {
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("name", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, "", false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("uuid", PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.SINGLE, "", false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("done", PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE, false, false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("tags", PropertyDescriptor.Type.UUID, new PropertyDescriptor.Subtype.LabelSubtype("tag"), PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(), false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("status", PropertyDescriptor.Type.UUID, new PropertyDescriptor.Subtype.LabelSubtype("status"), PropertyDescriptor.Multiplicity.SINGLE, null, false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("priority", PropertyDescriptor.Type.Integer, new PropertyDescriptor.Subtype.OrderedLabelSubtype("priority"), PropertyDescriptor.Multiplicity.SINGLE, null, false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("effort", PropertyDescriptor.Type.Integer, new PropertyDescriptor.Subtype.OrderedLabelSubtype("effort"), PropertyDescriptor.Multiplicity.SINGLE, null, false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("id", PropertyDescriptor.Type.Integer, null, PropertyDescriptor.Multiplicity.SINGLE, null, true));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("startDate", PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.DateSubtype(), PropertyDescriptor.Multiplicity.SINGLE, null, false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("startTime", PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.TimeSubtype(), PropertyDescriptor.Multiplicity.SINGLE, null, false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("dueDate", PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.DateSubtype(), PropertyDescriptor.Multiplicity.SINGLE, null, false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("dueTime", PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.TimeSubtype(), PropertyDescriptor.Multiplicity.SINGLE, null, false));
        propertyDescriptorUseCase.createPropertyDescriptor(
                new PropertyDescriptor("parent", PropertyDescriptor.Type.UUID, new PropertyDescriptor.Subtype.TaskSubtype(), PropertyDescriptor.Multiplicity.SINGLE, null, false));
    }

    private void initializePriorities() throws IOException {
        orderedLabelUseCase.deleteAllOrderedLabels("priority");
        orderedLabelUseCase.createOrderedLabel("priority", "low");
        orderedLabelUseCase.createOrderedLabel("priority", "medium");
        orderedLabelUseCase.createOrderedLabel("priority", "high");
    }

    private void initializeEfforts() throws IOException {
        orderedLabelUseCase.deleteAllOrderedLabels("effort");
        orderedLabelUseCase.createOrderedLabel("effort", "trivial");
        orderedLabelUseCase.createOrderedLabel("effort", "low");
        orderedLabelUseCase.createOrderedLabel("effort", "medium");
        orderedLabelUseCase.createOrderedLabel("effort", "high");
    }

    private void initializeViewInfo() throws IOException {
        viewInfoUseCase.deleteAllViewInfos();
        viewInfoUseCase.addViewInfo(new ViewInfo(
                "default",
                new SortingInfo(List.of(new SortingCriterion("name", true))),
                new FilterCriterionInfo("default", FilterCriterionInfo.Type.PROPERTY, "done", null, Predicate.EQUALS, List.of(false)),
                List.of("id", "name", "status", "tags", "dueDate"),
                OutputFormat.TEXT,
                false
        ));
        viewInfoUseCase.addViewInfo(new ViewInfo(
                "all",
                new SortingInfo(List.of(new SortingCriterion("name", true))),
                null,
                List.of("id", "name", "status", "tags", "dueDate"),
                OutputFormat.TEXT,
                false
        ));
    }

    private void initializeConfig() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));

        Map<String, Object> data = new HashMap<>();
        data.put("defaultView", "default");
        data.put("allowPropertyPrefix", true);
        data.put("allowCommandPrefix", true);
        data.put("commandAliases", Map.of("ls", "list"));

        Yaml yaml = new Yaml();
        yaml.dump(data, writer);

        configurationRepository.reload();
    }

    private final PropertyDescriptorUseCase propertyDescriptorUseCase;
    private final LabelUseCase labelUseCase;
    private final OrderedLabelUseCase orderedLabelUseCase;
    private final ViewInfoUseCase viewInfoUseCase;
    private final ConfigurationRepository configurationRepository;
    @Named("configurationYamlFile") private final File configFile;

}

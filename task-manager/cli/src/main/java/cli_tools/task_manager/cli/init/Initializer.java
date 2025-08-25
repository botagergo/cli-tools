package cli_tools.task_manager.cli.init;

import cli_tools.common.core.data.*;
import cli_tools.common.core.repository.ConfigurationRepository;
import cli_tools.common.label.service.LabelService;
import cli_tools.common.ordered_label.service.OrderedLabelService;
import cli_tools.common.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.pseudo_property_provider.TempIDPseudoPropertyProvider;
import cli_tools.common.temp_id_mapping.TempIDManager;
import cli_tools.common.view.service.ViewInfoService;
import cli_tools.task_manager.pseudo_property_provider.DonePseudoPropertyProvider;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class Initializer {

    private final PropertyDescriptorService propertyDescriptorService;
    private final LabelService labelService;
    private final OrderedLabelService orderedLabelService;
    private final ViewInfoService viewInfoService;
    private final ConfigurationRepository configurationRepository;
    private final TempIDManager tempIdManager;
    @Named("configurationYamlFile")
    private final File configFile;

    @Inject
    public Initializer(
            PropertyDescriptorService propertyDescriptorService,
            LabelService labelService,
            OrderedLabelService orderedLabelService,
            ConfigurationRepository configurationRepository,
            ViewInfoService viewInfoService,
            TempIDManager tempIdManager,
            @Named("configurationYamlFile") File configFile
    ) {
        this.propertyDescriptorService = propertyDescriptorService;
        this.labelService = labelService;
        this.orderedLabelService = orderedLabelService;
        this.viewInfoService = viewInfoService;
        this.configurationRepository = configurationRepository;
        this.tempIdManager = tempIdManager;
        this.configFile = configFile;
    }

    public boolean needsInitialization() throws IOException {
        List<PropertyDescriptor> propertyDescriptorCollection = propertyDescriptorService.getPropertyDescriptors();
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
        labelService.deleteAllLabels("status");
        labelService.createLabel("status", "NextAction");
        labelService.createLabel("status", "Waiting");
        labelService.createLabel("status", "Planning");
        labelService.createLabel("status", "OnHold");
    }

    private void initializePropertyDescriptors() throws IOException {
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("name", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, "", null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("uuid", PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.SINGLE, "", null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("tags", PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.LabelSubtype("tag"), PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(), null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("status", PropertyDescriptor.Type.String, new PropertyDescriptor.Subtype.LabelSubtype("status"), PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("priority", PropertyDescriptor.Type.Integer, new PropertyDescriptor.Subtype.OrderedLabelSubtype("priority"), PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("effort", PropertyDescriptor.Type.Integer, new PropertyDescriptor.Subtype.OrderedLabelSubtype("effort"), PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("id", PropertyDescriptor.Type.Integer, null, PropertyDescriptor.Multiplicity.SINGLE, null, new TempIDPseudoPropertyProvider(tempIdManager)));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("startDate", PropertyDescriptor.Type.Date, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("startTime", PropertyDescriptor.Type.Time, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("dueDate", PropertyDescriptor.Type.Date, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("dueTime", PropertyDescriptor.Type.Time, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("parent", PropertyDescriptor.Type.UUID, new PropertyDescriptor.Subtype.TaskSubtype(), PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("done", PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE, null, new DonePseudoPropertyProvider()));
    }

    private void initializePriorities() throws IOException {
        orderedLabelService.deleteAllOrderedLabels("priority");
        orderedLabelService.createOrderedLabel("priority", "Low");
        orderedLabelService.createOrderedLabel("priority", "Medium");
        orderedLabelService.createOrderedLabel("priority", "High");
    }

    private void initializeEfforts() throws IOException {
        orderedLabelService.deleteAllOrderedLabels("effort");
        orderedLabelService.createOrderedLabel("effort", "Trivial");
        orderedLabelService.createOrderedLabel("effort", "Low");
        orderedLabelService.createOrderedLabel("effort", "Medium");
        orderedLabelService.createOrderedLabel("effort", "High");
    }

    private void initializeViewInfo() throws IOException {
        viewInfoService.deleteAllViewInfos();
        viewInfoService.addViewInfo("default", ViewInfo.builder()
                .sortingInfo(new SortingInfo(List.of(new SortingCriterion("name", true))))
                .propertiesToList(List.of("id", "name", "status", "tags", "dueDate"))
                .outputFormat(OutputFormat.TEXT)
                .build());

        viewInfoService.addViewInfo("all", ViewInfo.builder()
                .sortingInfo(new SortingInfo(List.of(new SortingCriterion("name", true))))
                .propertiesToList(List.of("id", "name", "status", "tags", "dueDate"))
                .outputFormat(OutputFormat.TEXT)
                .listDone(true)
                .build());
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

}

package cli_tools.task_manager.cli.init;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.*;
import cli_tools.common.core.repository.ConfigurationRepository;
import cli_tools.common.backend.label.service.LabelService;
import cli_tools.common.backend.ordered_label.service.OrderedLabelService;
import cli_tools.common.backend.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.backend.pseudo_property_provider.TempIDPseudoPropertyProvider;
import cli_tools.common.backend.temp_id_mapping.TempIDManager;
import cli_tools.common.backend.view.service.ViewInfoService;
import cli_tools.task_manager.backend.pseudo_property_provider.DonePseudoPropertyProvider;
import cli_tools.task_manager.backend.task.Task;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public void initialize() throws IOException, ServiceException {
        initializePropertyDescriptors();
        initializeStatuses();
        initializePriorities();
        initializeEfforts();
        initializeViewInfo();
        initializeConfig();
    }

    private void initializeStatuses() throws ServiceException {
        labelService.deleteAllLabels(Task.STATUS);
        labelService.createLabel(Task.STATUS, "NextAction");
        labelService.createLabel(Task.STATUS, "Waiting");
        labelService.createLabel(Task.STATUS, "Planning");
        labelService.createLabel(Task.STATUS, "OnHold");
    }

    private void initializePropertyDescriptors() throws ServiceException {
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("name", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, "", null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor(Task.UUID

, PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor(Task.TAGS, PropertyDescriptor.Type.UUID, new PropertyDescriptor.Subtype.LabelSubtype("tag"), PropertyDescriptor.Multiplicity.SET, new LinkedHashSet<>(), null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor(Task.STATUS, PropertyDescriptor.Type.UUID, new PropertyDescriptor.Subtype.LabelSubtype(Task.STATUS), PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor(Task.PRIORITY, PropertyDescriptor.Type.Integer, new PropertyDescriptor.Subtype.OrderedLabelSubtype(Task.PRIORITY), PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor(Task.EFFORT, PropertyDescriptor.Type.Integer, new PropertyDescriptor.Subtype.OrderedLabelSubtype(Task.EFFORT), PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("id", PropertyDescriptor.Type.Integer, null, PropertyDescriptor.Multiplicity.SINGLE, null, new TempIDPseudoPropertyProvider(tempIdManager)));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor(Task.START_DATE, PropertyDescriptor.Type.Date, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor(Task.START_TIME, PropertyDescriptor.Type.Time, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor(Task.DUE_DATE, PropertyDescriptor.Type.Date, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor(Task.DUE_TIME, PropertyDescriptor.Type.Time, null, PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor(Task.PARENT, PropertyDescriptor.Type.UUID, new PropertyDescriptor.Subtype.TaskSubtype(), PropertyDescriptor.Multiplicity.SINGLE, null, null));
        propertyDescriptorService.createPropertyDescriptor(
                new PropertyDescriptor("done", PropertyDescriptor.Type.Boolean, null, PropertyDescriptor.Multiplicity.SINGLE, null, new DonePseudoPropertyProvider()));
    }

    private void initializePriorities() throws ServiceException {
        orderedLabelService.deleteAllOrderedLabels(Task.PRIORITY);
        orderedLabelService.createOrderedLabel(Task.PRIORITY, "Low", 0);
        orderedLabelService.createOrderedLabel(Task.PRIORITY, "Medium", 1);
        orderedLabelService.createOrderedLabel(Task.PRIORITY, "High", 2);
    }

    private void initializeEfforts() throws ServiceException {
        orderedLabelService.deleteAllOrderedLabels(Task.EFFORT);
        orderedLabelService.createOrderedLabel(Task.EFFORT, "Trivial", 0);
        orderedLabelService.createOrderedLabel(Task.EFFORT, "Low", 1);
        orderedLabelService.createOrderedLabel(Task.EFFORT, "Medium", 2);
        orderedLabelService.createOrderedLabel(Task.EFFORT, "High", 3);
    }

    private void initializeViewInfo() throws ServiceException {
        viewInfoService.deleteAllViewInfos();
        viewInfoService.addViewInfo("default", ViewInfo.builder()
                .sortingInfo(new SortingInfo(List.of(new SortingCriterion(Task.NAME, true))))
                .propertiesToList(List.of("id", Task.NAME, Task.STATUS, Task.TAGS, Task.DUE_DATE))
                .outputFormat(OutputFormat.TEXT)
                .build());

        viewInfoService.addViewInfo("all", ViewInfo.builder()
                .sortingInfo(new SortingInfo(List.of(new SortingCriterion(Task.NAME, true))))
                .propertiesToList(List.of("id", Task.NAME, Task.STATUS, Task.TAGS, Task.DUE_DATE))
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
        data.put("commandAliases", Map.of(
                "ls", "list",
                "del", "delete",
                "mod", "modify"
        ));

        Yaml yaml = new Yaml();
        yaml.dump(data, writer);

        configurationRepository.reload();
    }

}

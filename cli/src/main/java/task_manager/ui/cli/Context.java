package task_manager.ui.cli;

import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import task_manager.core.property.PropertyManager;
import task_manager.core.repository.ConfigurationRepository;
import task_manager.core.repository.TempIDMappingRepository;
import task_manager.logic.use_case.label.LabelUseCase;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCase;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import task_manager.logic.use_case.task.TaskUseCase;
import task_manager.logic.use_case.view.ViewInfoUseCase;
import task_manager.ui.cli.command.string_to_property_converter.StringToPropertyConverter;

public class Context {

    @Getter @Setter @Inject private TaskUseCase taskUseCase;

    @Getter @Setter @Inject private LabelUseCase labelUseCase;

    @Getter @Setter @Inject private OrderedLabelUseCase orderedLabelUseCase;

    @Getter @Setter @Inject private PropertyDescriptorUseCase propertyDescriptorUseCase;

    @Getter @Setter @Inject private ViewInfoUseCase viewInfoUseCase;

    @Getter @Setter @Inject private PropertyManager propertyManager;

    @Getter @Setter @Inject private StringToPropertyConverter stringToPropertyConverter;

    @Getter @Setter @Inject private TempIDMappingRepository tempIDMappingRepository;

    @Getter @Setter @Inject private ConfigurationRepository configurationRepository;

}

package task_manager.ui.cli;

import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import task_manager.logic.use_case.status.StatusUseCase;
import task_manager.logic.use_case.tag.TagUseCase;
import task_manager.logic.use_case.task.TaskUseCase;
import task_manager.property.PropertyManager;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.repository.TempIDMappingRepository;
import task_manager.ui.cli.command.property_converter.StringToPropertyConverter;

public class Context {

    @Getter @Setter @Inject
    private TaskUseCase taskUseCase;

    @Getter @Setter @Inject
    private StatusUseCase statusUseCase;

    @Getter @Setter @Inject
    private TagUseCase tagUseCase;

    @Getter @Setter @Inject
    private PropertyDescriptorUseCase propertyDescriptorUseCase;

    @Getter @Setter @Inject
    private PropertyManager propertyManager;

    @Getter @Setter @Inject
    private StringToPropertyConverter stringToPropertyConverter;

    @Getter @Setter @Inject
    private LabelRepositoryFactory labelRepositoryFactory;

    @Getter @Setter @Inject
    private TempIDMappingRepository tempIDMappingRepository;

}

package task_manager.ui.cli;

import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import task_manager.property.PropertyManager;
import task_manager.logic.use_case.PropertyDescriptorUseCase;
import task_manager.logic.use_case.StatusUseCase;
import task_manager.logic.use_case.TagUseCase;
import task_manager.logic.use_case.TaskUseCase;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.ui.cli.command.property_converter.PropertyConverter;

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
    private PropertyConverter propertyConverter;

    @Getter @Setter @Inject
    private LabelRepositoryFactory labelRepositoryFactory;

}

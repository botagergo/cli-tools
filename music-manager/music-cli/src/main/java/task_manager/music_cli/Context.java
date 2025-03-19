package common.music_cli;

import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import common.core.repository.ConfigurationRepository;
import common.music_logic.use_case.label.LabelUseCase;
import common.music_logic.use_case.ordered_label.OrderedLabelUseCase;
import common.music_logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import common.music_logic.use_case.task.TaskUseCase;
import common.music_logic.use_case.temp_id_mapping.TempIDMappingUseCase;
import common.music_logic.use_case.view.ViewInfoUseCase;
import common.property_lib.PropertyManager;
import common.music_cli.command.string_to_property_converter.StringToPropertyConverter;

@Setter
@Getter
public class Context {

    @Inject private TaskUseCase taskUseCase;

    @Inject private LabelUseCase labelUseCase;

    @Inject private OrderedLabelUseCase orderedLabelUseCase;

    @Inject private PropertyDescriptorUseCase propertyDescriptorUseCase;

    @Inject private ViewInfoUseCase viewInfoUseCase;

    @Inject private PropertyManager propertyManager;

    @Inject private StringToPropertyConverter stringToPropertyConverter;

    @Inject private TempIDMappingUseCase tempIDMappingUseCase;

    @Inject private ConfigurationRepository configurationRepository;

    private Integer getPrevID = null;

}

package task_manager.task_manager_cli;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.FunctionExecutor;
import com.theokanning.openai.service.OpenAiService;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import task_manager.core.repository.ConfigurationRepository;
import task_manager.task_logic.use_case.label.LabelUseCase;
import task_manager.task_logic.use_case.ordered_label.OrderedLabelUseCase;
import task_manager.task_logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import task_manager.task_logic.use_case.task.TaskUseCase;
import task_manager.task_logic.use_case.temp_id_mapping.TempIDMappingUseCase;
import task_manager.task_logic.use_case.view.ViewInfoUseCase;
import task_manager.property_lib.PropertyManager;
import task_manager.task_manager_cli.command.string_to_property_converter.StringToPropertyConverter;

import java.util.List;

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

    private OpenAiService openAiService;

    private List<ChatMessage> openAiChatMessages;

    private FunctionExecutor openAiFunctionExecutor;

    private Integer prevTaskID = null;

}

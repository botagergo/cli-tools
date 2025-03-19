package cli_tools.task_manager.cli;

import cli_tools.task_manager.cli.command.TaskPrinter;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.FunctionExecutor;
import com.theokanning.openai.service.OpenAiService;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import cli_tools.common.cli.property_to_string_converter.JsonPropertyToStringConverterRepository;
import cli_tools.common.cli.string_to_property_converter.StringToPropertyConverter;
import cli_tools.common.core.repository.ConfigurationRepository;
import cli_tools.common.label.service.LabelService;
import cli_tools.common.ordered_label.service.OrderedLabelService;
import cli_tools.common.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.temp_id_mapping.service.TempIDMappingService;
import cli_tools.common.view.service.ViewInfoService;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.task_manager.task.service.TaskService;

import java.util.List;

@ToString
@Setter
@Getter
public class Context {

    @Inject private TaskService taskService;

    @Inject private LabelService labelService;

    @Inject private OrderedLabelService orderedLabelService;

    @Inject private PropertyDescriptorService propertyDescriptorService;

    @Inject private ViewInfoService viewInfoService;

    @Inject private PropertyManager propertyManager;

    @Inject private StringToPropertyConverter stringToPropertyConverter;

    @Inject private TempIDMappingService tempIDMappingService;

    @Inject private JsonPropertyToStringConverterRepository propertyToStringConverterRepository;

    @Inject private ConfigurationRepository configurationRepository;

    @Inject private TaskPrinter taskPrinter;

    private OpenAiService openAiService;

    private List<ChatMessage> openAiChatMessages;

    private FunctionExecutor openAiFunctionExecutor;

    private Integer prevTaskID = null;

}

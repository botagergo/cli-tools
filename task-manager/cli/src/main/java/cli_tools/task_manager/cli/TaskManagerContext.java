package cli_tools.task_manager.cli;

import cli_tools.task_manager.cli.command.TaskPrinter;
import cli_tools.task_manager.task.service.TaskService;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.FunctionExecutor;
import com.theokanning.openai.service.OpenAiService;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Setter
@Getter
public class TaskManagerContext extends cli_tools.common.cli.Context {

    @Inject
    private TaskService taskService;

    @Inject
    private TaskPrinter taskPrinter;

    private OpenAiService openAiService;

    private List<ChatMessage> openAiChatMessages;

    private FunctionExecutor openAiFunctionExecutor;

}

package cli_tools.task_manager.cli.command;

import cli_tools.common.cli.command.Command;
import cli_tools.common.core.util.Print;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cli_tools.common.core.data.OutputFormat;
import cli_tools.task_manager.task.Task;
import cli_tools.task_manager.task.service.TaskService;
import cli_tools.task_manager.cli.TaskManagerContext;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor
@Getter
@Setter
public final class AICommand extends Command {
    @Override
    public void execute(cli_tools.common.cli.Context context) {
        try {
            List<ChatFunctionDynamic> functions = buildFunctions();
            TaskManagerContext taskManagerContext = (TaskManagerContext) context;

            if (taskManagerContext.getOpenAiService() == null) {
                String openAiApiKey = context.getConfigurationRepository().openAiApiKey();
                if (openAiApiKey == null) {
                    Print.printError("OpenAI API key is not configured, set it with the 'openAiApiKey' config option");
                    return;
                }
                taskManagerContext.setOpenAiService(new OpenAiService(openAiApiKey));
                taskManagerContext.setOpenAiChatMessages(new ArrayList<>());

                taskManagerContext.getOpenAiChatMessages().add(new ChatMessage(ChatMessageRole.USER.value(),
                        "I will give you commands to retrieve data from my task database. You will need to call the appropriate functions to retrieve the data. When you have all the info to answer, you MUST call the provide_response function, and fill the taskList argument with the appropriate tasks."));
                ChatCompletionRequest request = buildRequest(taskManagerContext, functions, taskManagerContext.getOpenAiChatMessages());

                taskManagerContext.getOpenAiService().createChatCompletion(request);
            }

            taskManagerContext.getOpenAiChatMessages().add(new ChatMessage(ChatMessageRole.USER.value(), command));

            ChatCompletionRequest request = buildRequest(taskManagerContext, functions, taskManagerContext.getOpenAiChatMessages());

            ChatCompletionResult result = taskManagerContext.getOpenAiService().createChatCompletion(request);
            ChatMessage response = result.getChoices().get(0).getMessage();
            ChatFunctionCall functionCall = response.getFunctionCall();
            if (functionCall != null) {
                ChatMessage functionResponseMessage = executeFunction(taskManagerContext, functionCall);
                if (functionResponseMessage != null) {
                    taskManagerContext.getOpenAiChatMessages().add(functionResponseMessage);
                }

                request = buildRequest(taskManagerContext, functions, taskManagerContext.getOpenAiChatMessages());
                response = taskManagerContext.getOpenAiService().createChatCompletion(request).getChoices().get(0).getMessage();
                taskManagerContext.getOpenAiChatMessages().add(response);
            }

            functionCall = response.getFunctionCall();
            if (functionCall != null) {
                executeFunction(taskManagerContext, functionCall);
            }

        } catch (Exception e) {
            Print.printError(e.getMessage());
        }
    }

    private List<ChatFunctionDynamic> buildFunctions() {
        List<ChatFunctionDynamic> functions = new ArrayList<>();

        functions.add(ChatFunctionDynamic.builder()
                .name("query_tasks")
                .description("Query the list of tasks from the database")
                .build());

        functions.add(ChatFunctionDynamic.builder()
                .name("list_tasks")
                .description("This function is used to provide the queried tasks to the user. Contains the filtered list of tasks.")
                .addProperty(ChatFunctionProperty.builder()
                        .type("array")
                        .name("taskUuids")
                        .items(ChatFunctionProperty.builder()
                                .name("sdfs")
                                .type("string").build()).build()
                ).build());

        functions.add(ChatFunctionDynamic.builder()
                .name("add_tasks")
                .description("This function is used to provide the list of tasks to create. The tasks to create must be passed as arguments")
                .addProperty(ChatFunctionProperty.builder()
                        .type("array")
                        .name("tasks")
                        .required(true)
                        .items(ChatFunctionProperty.builder()
                                .type("object")
                                .name("sdfs")
                                .items(ChatFunctionProperty.builder()
                                        .name("name")
                                        .required(true)
                                        .type("string").build())
                                .build()).build()
                ).build());

        return functions;
    }

    private ChatCompletionRequest buildRequest(TaskManagerContext context, List<ChatFunctionDynamic> functions, List<ChatMessage> messages) {
        String model = context.getConfigurationRepository().openAiModel();
        if (model == null) {
            model = "gpt-3.5-turbo-0613";
        }
        return ChatCompletionRequest.builder()
                .model(model)
                .functions(functions)
                .functionCall(new ChatCompletionRequest.ChatCompletionRequestFunctionCall("auto"))
                .messages(messages)
                .build();
    }

    private ChatMessage executeFunction(TaskManagerContext context, ChatFunctionCall functionCall) throws IOException {
        if (functionCall.getName().equals("list_tasks")) {
            listTasks(context, functionCall.getArguments());
        } else if (functionCall.getName().equals("add_tasks")) {
            addTasks(context, functionCall.getArguments());
        } else if (functionCall.getName().equals("query_tasks")) {
            List<Task> tasks = context.getTaskService().getTasks();
            ObjectMapper objectMapper = new ObjectMapper();
            return new ChatMessage(
                    ChatMessageRole.FUNCTION.value(),
                    objectMapper.writeValueAsString(tasks),
                    "query_tasks");
        }
        return null;
    }

    private void listTasks(TaskManagerContext context, JsonNode arguments) {
        try {
            List<String> propertiesToList = List.of("name", "status", "tags");
            List<UUID> taskUuidsList = new ArrayList<>();
            for (JsonNode jsonNode : arguments.get("taskUuids")) {
                taskUuidsList.add(UUID.fromString(jsonNode.asText()));
            }

            List<Task> tasks = context.getTaskService().getTasks(
                    null, null, null,
                    taskUuidsList);
            context.getTaskPrinter().printTasks(context, tasks, propertiesToList, OutputFormat.TEXT);
        } catch (Exception e) {
            Print.printError(e.getMessage());
        }
    }

    private void addTasks(TaskManagerContext context, JsonNode arguments) {
        try {
            TaskService taskService = context.getTaskService();
            for (JsonNode task : arguments.get("tasks")) {
                HashMap<String, Object> taskMap = new HashMap<>();
                for (Iterator<String> it = task.fieldNames(); it.hasNext(); ) {
                    String propertyName = it.next();
                    taskMap.put(propertyName, task.get(propertyName));
                }
                taskService.addTask(Task.fromMap(taskMap));
            }
        } catch (Exception e) {
            Print.printError(e.getMessage());
        }
    }

    private String command;

}

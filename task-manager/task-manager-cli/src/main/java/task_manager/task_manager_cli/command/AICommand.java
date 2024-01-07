package task_manager.task_manager_cli.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import task_manager.core.data.OutputFormat;
import task_manager.core.data.Task;
import task_manager.task_logic.use_case.task.TaskUseCase;
import task_manager.task_manager_cli.Context;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor
@Getter
@Setter
public final class AICommand extends Command {
    @Override
    public void execute(Context context) {
        try {
            List<ChatFunctionDynamic> functions = buildFunctions();

            if (context.getOpenAiService() == null) {
                String openAiApiKey = context.getConfigurationRepository().openAiApiKey();
                if (openAiApiKey == null) {
                    System.out.println("OpenAI API key is not configured. Set it with the \"openAiApiKey\" config option.");
                    return;
                }
                context.setOpenAiService(new OpenAiService(openAiApiKey));
                context.setOpenAiChatMessages(new ArrayList<>());

                context.getOpenAiChatMessages().add(new ChatMessage(ChatMessageRole.USER.value(),
                        "I will give you commands to retrieve data from my task database. You will need to call the appropriate functions to retrieve the data. When you have all the info to answer, you MUST call the provide_response function, and fill the taskList argument with the appropriate tasks."));
                ChatCompletionRequest request = buildRequest(context, functions, context.getOpenAiChatMessages());

                context.getOpenAiService().createChatCompletion(request);
            }

            context.getOpenAiChatMessages().add(new ChatMessage(ChatMessageRole.USER.value(), command));

            ChatCompletionRequest request = buildRequest(context, functions, context.getOpenAiChatMessages());

            ChatCompletionResult result = context.getOpenAiService().createChatCompletion(request);
            ChatMessage response = result.getChoices().get(0).getMessage();
            ChatFunctionCall functionCall = response.getFunctionCall();
            if (functionCall != null) {
                ChatMessage functionResponseMessage = executeFunction(context, functionCall);
                if (functionResponseMessage != null) {
                    context.getOpenAiChatMessages().add(functionResponseMessage);
                }

                request = buildRequest(context, functions, context.getOpenAiChatMessages());
                response = context.getOpenAiService().createChatCompletion(request).getChoices().get(0).getMessage();
                context.getOpenAiChatMessages().add(response);
            }

            functionCall = response.getFunctionCall();
            if (functionCall != null) {
                executeFunction(context, functionCall);
            }

            //System.out.println(response.getContent());
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

    private ChatCompletionRequest buildRequest(Context context, List<ChatFunctionDynamic> functions, List<ChatMessage> messages) {
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

    private ChatMessage executeFunction(Context context, ChatFunctionCall functionCall) throws IOException {
        if (functionCall.getName().equals("list_tasks")) {
            listTasks(context, functionCall.getArguments());
        } else if (functionCall.getName().equals("add_tasks")) {
            addTasks(context, functionCall.getArguments());
        } else if (functionCall.getName().equals("query_tasks")) {
            List<Task> tasks = context.getTaskUseCase().getTasks();
            ObjectMapper objectMapper = new ObjectMapper();
            return new ChatMessage(
                    ChatMessageRole.FUNCTION.value(),
                    objectMapper.writeValueAsString(tasks),
                    "query_tasks");
        }
        return null;
    }

    private void listTasks(Context context, JsonNode arguments) {
        try {
            List<String> propertiesToList = List.of("name", "status", "tags");
            List<UUID> taskUuidsList = new ArrayList<>();
            for (JsonNode jsonNode : arguments.get("taskUuids")) {
                taskUuidsList.add(UUID.fromString(jsonNode.asText()));
            }

            List<Task> tasks = context.getTaskUseCase().getTasks(
                    null, null, null,
                    taskUuidsList);
            context.getTaskPrinter().printTasks(context, tasks, propertiesToList, OutputFormat.TEXT);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void addTasks(Context context, JsonNode arguments) {
        try {
            TaskUseCase taskUseCase = context.getTaskUseCase();
            for (JsonNode task : arguments.get("tasks")) {
                HashMap<String, Object> taskMap = new HashMap<>();
                for (Iterator<String> it = task.fieldNames(); it.hasNext(); ) {
                    String propertyName = it.next();
                    taskMap.put(propertyName, task.get(propertyName));
                }
                taskUseCase.addTask(Task.fromMap(taskMap));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String command;

}

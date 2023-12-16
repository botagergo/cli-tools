package task_manager.task_logic.use_case;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TaskManagerClient {

    public TaskManagerClient(String address) {
        this.address = address;
    }

    public <T> T postJson(String path, Object content, Class<T> contentClass)
            throws IOException, URISyntaxException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(content);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(address + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), contentClass);
    }

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String address;

}

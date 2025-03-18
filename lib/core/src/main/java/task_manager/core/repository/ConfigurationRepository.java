package task_manager.core.repository;

import java.util.Map;

public interface ConfigurationRepository {
    String defaultView();

    boolean allowPropertyPrefix();

    boolean allowCommandPrefix();

    String openAiModel();

    String openAiApiKey();

    Map<String, String> commandAliases();
}
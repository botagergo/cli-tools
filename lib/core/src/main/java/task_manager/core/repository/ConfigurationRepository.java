package task_manager.core.repository;

public interface ConfigurationRepository {
    String defaultView();
    boolean allowPropertyPrefix();
    String openAiModel();
    String openAiApiKey();
}

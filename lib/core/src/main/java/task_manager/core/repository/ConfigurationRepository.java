package task_manager.core.repository;

import java.util.TimeZone;

public interface ConfigurationRepository {
    String defaultView();
    boolean allowPropertyPrefix();
    String openAiModel();
    String openAiApiKey();
    TimeZone timeZone();
}

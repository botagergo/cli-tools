package cli_tools.common.core.repository;

import java.io.IOException;
import java.util.Map;

public interface ConfigurationRepository {
    String defaultView();

    boolean allowPropertyPrefix();

    boolean allowCommandPrefix();

    String openAiModel();

    String openAiApiKey();

    Map<String, String> commandAliases();

    void reload() throws IOException;
}
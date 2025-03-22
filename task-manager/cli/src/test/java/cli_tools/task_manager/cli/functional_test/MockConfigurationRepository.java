package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.core.repository.ConfigurationRepository;

import java.util.Map;

public class MockConfigurationRepository implements ConfigurationRepository {

    public String defaultView = "default";
    public boolean allowPropertyPrefix = false;
    public boolean allowCommandPrefix = false;
    public Map<String, String> commandAliases = Map.of();
    public boolean disableConfirmation = true;

    @Override
    public String defaultView() {
        return defaultView;
    }

    @Override
    public boolean allowPropertyPrefix() {
        return allowPropertyPrefix;
    }

    @Override
    public boolean allowCommandPrefix() {
        return allowCommandPrefix;
    }

    @Override
    public String openAiModel() {
        return null;
    }

    @Override
    public String openAiApiKey() {
        return null;
    }

    @Override
    public Map<String, String> commandAliases() {
        return commandAliases;
    }

    @Override
    public boolean disableConfirmation() {
        return disableConfirmation;
    }

    @Override
    public void reload() {

    }

}

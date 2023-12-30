package task_manager.repository;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;
import task_manager.core.repository.ConfigurationRepository;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;

public class ConfigurationRepositoryImpl implements ConfigurationRepository {

    @Inject
    public ConfigurationRepositoryImpl(@Named("configurationYamlFile") File configFile) {
        ConfigFilesProvider configFilesProvider = () -> List.of(configFile.toPath());
        ConfigurationSource configurationSource = new FilesConfigurationSource(configFilesProvider);
        configProvider = new ConfigurationProviderBuilder()
                .withConfigurationSource(configurationSource)
                .build();
    }

    public String defaultView() {
        return getProperty("defaultView", String.class, null);
    }

    public boolean allowPropertyPrefix() {
        return getProperty("allowPropertyPrefix", Boolean.class, true);
    }

    public String openAiModel() { return getProperty("openAiModel", String.class, null); }

    public String openAiApiKey() {
        return getProperty("openAiApiKey", String.class, null);
    }

    public TimeZone timeZone() {
        String timeZone = getProperty("timeZone", String.class, null);
        return TimeZone.getTimeZone(timeZone);
    }

    private <T> T getProperty(String propertyName, Class<T> type, T defValue) {
        try {
            return configProvider.getProperty(propertyName, type);
        } catch (NoSuchElementException|IllegalStateException e) {
            return defValue;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to get config parameter '" + propertyName + "': " + e.getMessage());
        }
    }

    private final ConfigurationProvider configProvider;

}

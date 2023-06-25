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
        try {
            return configProvider.getProperty("defaultView", String.class);
        } catch (NoSuchElementException|IllegalStateException e) {
            return null;
        }
    }

    private final ConfigurationProvider configProvider;

}

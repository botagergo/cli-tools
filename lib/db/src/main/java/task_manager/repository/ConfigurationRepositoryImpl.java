package task_manager.repository;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.log4j.Log4j2;
import org.github.gestalt.config.Gestalt;
import org.github.gestalt.config.builder.GestaltBuilder;
import org.github.gestalt.config.exceptions.GestaltException;
import org.github.gestalt.config.reflect.TypeCapture;
import org.github.gestalt.config.source.FileConfigSourceBuilder;
import task_manager.core.repository.ConfigurationRepository;

import java.io.File;
import java.util.*;

@Log4j2
public class ConfigurationRepositoryImpl implements ConfigurationRepository {

    @Inject
    public ConfigurationRepositoryImpl(@Named("configurationYamlFile") File configFile) {
        try {
            gestalt = new GestaltBuilder()
                    .addSource(FileConfigSourceBuilder.builder().setFile(configFile).build())
                    .build();
            gestalt.loadConfigs();
        } catch (GestaltException e) {
            throw new RuntimeException(e);
        }
    }

    public String defaultView() {
        return getProperty("defaultView", String.class, null);
    }

    public boolean allowPropertyPrefix() {
        return getProperty("allowPropertyPrefix", Boolean.class, true);
    }

    public boolean allowCommandPrefix() {
        return getProperty("allowCommandPrefix", Boolean.class, true);
    }

    public String openAiModel() { return getProperty("openAiModel", String.class, null); }

    public String openAiApiKey() {
        return getProperty("openAiApiKey", String.class, null);
    }

    public Map<String, String> commandAliases() {
        return getPropertyWithGenericType("commandAliases", new TypeCapture<>() {}, Map.of());
    }

    private <T> T getProperty(String propertyName, Class<T> type, T defValue) {
        try {
            return gestalt.getConfig(propertyName, type);
        } catch (NoSuchElementException e) {
            log.debug("ConfigurationRepositoryImpl.getProperty - property \"" + propertyName + "\" not found, returning default");
            return defValue;
        } catch (GestaltException e) {
            log.error("ConfigurationRepositoryImpl.getProperty - unable to get config parameter '" + propertyName + ", returning default': " + e.getMessage());
            return defValue;
        }
    }

    private <T> T getPropertyWithGenericType(String propertyName, TypeCapture<T> typeCapture, T defValue) {
        try {
            return gestalt.getConfig(propertyName, typeCapture);
        } catch (NoSuchElementException e) {
            log.debug("ConfigurationRepositoryImpl.getProperty - property \"" + propertyName + "\" not found, returning default");
            return defValue;
        } catch (GestaltException e) {
            log.error("ConfigurationRepositoryImpl.getProperty - unable to get config parameter '" + propertyName + ", returning default': " + e.getMessage());
            return defValue;
        }
    }

    private final Gestalt gestalt;

}

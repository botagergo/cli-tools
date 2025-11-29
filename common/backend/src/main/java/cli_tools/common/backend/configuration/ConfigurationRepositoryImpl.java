package cli_tools.common.backend.configuration;

import cli_tools.common.core.repository.ConfigurationRepository;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.github.gestalt.config.Gestalt;
import org.github.gestalt.config.builder.GestaltBuilder;
import org.github.gestalt.config.exceptions.GestaltException;
import org.github.gestalt.config.reflect.TypeCapture;
import org.github.gestalt.config.source.FileConfigSourceBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Log4j2
public class ConfigurationRepositoryImpl implements ConfigurationRepository {

    private final Gestalt gestalt;
    @Getter private final File configFile;

    @Inject
    public ConfigurationRepositoryImpl(@Named("configurationYamlFile") File configFile) throws IOException {
        this.configFile = configFile;

        if (!configFile.exists()) {
            File parentFile = configFile.getParentFile();
            if (parentFile != null && !parentFile.isDirectory()) {
                //noinspection ResultOfMethodCallIgnored
                parentFile.mkdirs();
            }
            if (!configFile.createNewFile()) {
                log.warn("Failed to create config file at {}", configFile);
            }
        }
        try {
            gestalt = new GestaltBuilder()
                    .addSource(FileConfigSourceBuilder.builder().setFile(configFile).build())
                    .build();
            gestalt.loadConfigs();
        } catch (GestaltException e) {
            throw new IOException(e);
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

    public String openAiModel() {
        return getProperty("openAiModel", String.class, null);
    }

    public String openAiApiKey() {
        return getProperty("openAiApiKey", String.class, null);
    }

    public Map<String, String> commandAliases() {
        return getPropertyWithGenericType("commandAliases", new TypeCapture<>() {
        }, Map.of());
    }

    public boolean disableConfirmation() {
        return getProperty("disableConfirmation", Boolean.class, false);
    }

    public void reload() throws IOException {
        try {
            gestalt.loadConfigs();
        } catch (GestaltException e) {
            throw new IOException(e);
        }
    }

    private <T> T getProperty(String propertyName, Class<T> type, T defValue) {
        try {
            return gestalt.getConfig(propertyName, type);
        } catch (GestaltException e) {
            handleException(e, propertyName);
            return defValue;
        }
    }

    private <T> T getPropertyWithGenericType(String propertyName, TypeCapture<T> typeCapture, T defValue) {
        try {
            return gestalt.getConfig(propertyName, typeCapture);
        } catch (GestaltException e) {
            handleException(e, propertyName);
            return defValue;
        }
    }

    private void handleException(GestaltException e, String propertyName) {
        log.debug("property '{}' not found, returning default value", propertyName);
        log.trace(e.getMessage());
        log.trace(ExceptionUtils.getStackTrace(e));
    }

}

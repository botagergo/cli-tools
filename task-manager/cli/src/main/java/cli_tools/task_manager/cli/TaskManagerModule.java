package cli_tools.task_manager.cli;

import cli_tools.common.backend.ObjectMapperHelper;
import cli_tools.common.backend.label.repository.PostgresLabelRepository;
import cli_tools.common.backend.ordered_label.repository.PostgresOrderedLabelRepository;
import cli_tools.common.backend.property_descriptor.repository.PostgresPropertyDescriptorRepository;
import cli_tools.common.backend.property_descriptor.repository.SubtypeMixIn;
import cli_tools.common.backend.repository.MapDeserializer;
import cli_tools.common.backend.repository.MapSerializer;
import cli_tools.common.cli.Context;
import cli_tools.common.cli.command.custom_command.CustomCommandParserFactory;
import cli_tools.common.cli.command.custom_command.repository.CustomCommandRepository;
import cli_tools.common.cli.command.custom_command.repository.JsonCustomCommandRepository;
import cli_tools.common.cli.command_line.*;
import cli_tools.common.cli.command_parser.CommandParserFactory;
import cli_tools.common.cli.command_parser.CommandParserFactoryImpl;
import cli_tools.common.cli.executor.Executor;
import cli_tools.common.cli.executor.LocalExecutor;
import cli_tools.common.cli.tokenizer.Tokenizer;
import cli_tools.common.cli.tokenizer.TokenizerImpl;
import cli_tools.common.backend.configuration.ConfigurationRepositoryImpl;
import cli_tools.common.core.repository.*;
import cli_tools.common.backend.label.repository.JsonLabelRepository;
import cli_tools.common.backend.label.service.LabelService;
import cli_tools.common.backend.label.service.LabelServiceImpl;
import cli_tools.common.backend.ordered_label.repository.JsonOrderedLabelRepository;
import cli_tools.common.backend.ordered_label.service.OrderedLabelService;
import cli_tools.common.backend.ordered_label.service.OrderedLabelServiceImpl;
import cli_tools.common.backend.property_converter.PropertyConverter;
import cli_tools.common.backend.property_descriptor.repository.JsonPropertyDescriptorRepository;
import cli_tools.common.backend.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.backend.property_descriptor.service.PropertyDescriptorServiceImpl;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.backend.temp_id_mapping.TempIDManager;
import cli_tools.common.property_lib.PseudoPropertyProvider;
import cli_tools.common.util.RandomUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.backend.view.repository.JsonViewInfoRepository;
import cli_tools.common.backend.view.service.ViewInfoService;
import cli_tools.common.backend.view.service.ViewInfoServiceImpl;
import cli_tools.task_manager.backend.task.repository.PostgresTaskRepository;
import cli_tools.task_manager.cli.command.custom_command.CustomCommandDefinitionMixIn;
import cli_tools.task_manager.cli.command.custom_command.CustomCommandParserFactoryImpl;
import cli_tools.task_manager.backend.pseudo_property_provider.PseudoPropertyProviderMixIn;
import cli_tools.task_manager.backend.task.repository.JsonTaskRepository;
import cli_tools.task_manager.backend.task.repository.TaskRepository;
import cli_tools.task_manager.backend.task.service.TaskService;
import cli_tools.task_manager.backend.task.service.TaskServiceImpl;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;

public class TaskManagerModule extends AbstractModule {
    private final TaskManagerConfig taskManagerConfig;

    public TaskManagerModule(
            TaskManagerConfig taskManagerConfig
    ) {
        this.taskManagerConfig = taskManagerConfig;
        if (this.taskManagerConfig.getProfile() == null) {
            this.taskManagerConfig.setProfile("default");
        }
        if (this.taskManagerConfig.getPostgresqlUrl() == null) {
            this.taskManagerConfig.setPostgresqlUrl("jdbc:postgresql://postgres:5432/task_manager_db");
        }
        if (this.taskManagerConfig.getPostgresqlUsername() == null) {
            this.taskManagerConfig.setPostgresqlUsername("postgres");
        }
        if (this.taskManagerConfig.getPostgresqlPassword() == null) {
            this.taskManagerConfig.setPostgresqlPassword("12345");
        }
    }

    @Provides
    @Singleton
    CustomCommandRepository customCommandRepository() {
        JsonCustomCommandRepository jsonCustomCommandRepository =
                new JsonCustomCommandRepository(OsDirs.getFile(OsDirs.DirType.DATA, taskManagerConfig.getProfile(), "custom_command_definition.json"));
        jsonCustomCommandRepository.setMixIn(CustomCommandDefinitionMixIn.class);
        return jsonCustomCommandRepository;
    }

    TaskRepository taskRepository(ObjectMapper objectMapper, UUIDGenerator uuidGenerator, boolean done) {
        switch (taskManagerConfig.getDatabaseMode()) {
            case JSON -> {
                return new JsonTaskRepository(OsDirs.getFile(
                        OsDirs.DirType.DATA, taskManagerConfig.getProfile(), done ? "done_task.json" : "task.json"),
                        uuidGenerator);
            }
            case POSTGRESQL -> {
                JavaType javaType = objectMapper.getTypeFactory().constructType(HashMap.class);
                return new PostgresTaskRepository(dataSource(), objectMapper.readerFor(javaType), objectMapper.writerFor(javaType), done);
            }
            default -> throw new RuntimeException();
        }
    }

    @Provides
    @Singleton
    ObjectMapperHelper objectMapperHelper() {
        return new ObjectMapperHelper(PseudoPropertyProviderMixIn.class);
    }

    @Provides
    @Singleton
    TaskService taskService(
            PropertyManager propertyManager,
            UUIDGenerator uuidGenerator,
            PropertyConverter propertyConverter,
            @Named("propertyDescriptorObjectMapper") ObjectMapper objectMapper,
            TempIDManager tempIdManager) {
        return new TaskServiceImpl(
                taskRepository(objectMapper, uuidGenerator, false),
                taskRepository(objectMapper, uuidGenerator, true),
                propertyManager,
                uuidGenerator,
                propertyConverter,
                tempIdManager);
    }



    @Provides
    @Singleton
    @Named("propertyDescriptorObjectMapper")
    ObjectMapper propertyDescriptorObjectMapper(TempIDManager tempIdManager) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(PropertyDescriptor.Subtype.class, SubtypeMixIn.class);
        objectMapper.addMixIn(PseudoPropertyProvider.class, PseudoPropertyProviderMixIn.class);
        InjectableValues injectableValues = new InjectableValues.Std()
                .addValue(TempIDManager.class, tempIdManager);
        objectMapper.setInjectableValues(injectableValues);
        return objectMapper;
    }

    @Provides
    @Singleton
    @Named("propertyMapObjectMapper")
    ObjectMapper propertyMapObjectMapper(TempIDManager tempIdManager) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new MapSerializer());
        simpleModule.addDeserializer(HashMap .class, new MapDeserializer());
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    @Provides
    @Singleton
    PropertyDescriptorRepository propertyDescriptorRepository(
            TempIDManager tempIdManager,
            ObjectMapperHelper objectMapperHelper,
            @Named("propertyDescriptorJsonFile") File propertyDescriptorFile
    ) {
        switch (taskManagerConfig.getDatabaseMode()) {
            case JSON -> {
                return new JsonPropertyDescriptorRepository(
                        propertyDescriptorFile,
                        tempIdManager,
                        PseudoPropertyProviderMixIn.class);
            }
            case POSTGRESQL -> {
                return new PostgresPropertyDescriptorRepository(dataSource(), objectMapperHelper);
            }
            default -> throw new RuntimeException();
        }
    }

    @Provides
    CommandLine jlineCommandLine(Context context, Executor executor) {
        return new JlineCommandLine(
                executor,
                completer(context),
                OsDirs.getFile(OsDirs.DirType.CACHE, taskManagerConfig.getProfile(), "history")
        );
    }

    @Provides
    LabelRepository labelRepository(UUIDGenerator uuidGenerator) {
        switch (taskManagerConfig.getDatabaseMode()) {
            case JSON -> {
                return new JsonLabelRepository(uuidGenerator, OsDirs.getFile(OsDirs.DirType.DATA, taskManagerConfig.getProfile(),"label.json"));
            }
            case POSTGRESQL -> {
                return new PostgresLabelRepository(dataSource());
            }
            default -> throw new RuntimeException();
        }
    }

    @Provides
    OrderedLabelRepository orderedLabelRepository() {
        switch (taskManagerConfig.getDatabaseMode()) {
            case JSON -> {
                return new JsonOrderedLabelRepository(OsDirs.getFile(OsDirs.DirType.DATA, taskManagerConfig.getProfile(),"ordered_label.json"));
            }
            case POSTGRESQL -> {
                return new PostgresOrderedLabelRepository(dataSource());
            }
            default -> throw new RuntimeException();
        }
    }

    @Provides
    Completer completer(Context context) {
        return new cli_tools.common.cli.command_line.Completer(context);
    }

    @Provides
    @Singleton
    private DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(taskManagerConfig.getPostgresqlUrl());
        hikariConfig.setUsername(taskManagerConfig.getPostgresqlUsername());
        hikariConfig.setPassword(taskManagerConfig.getPostgresqlPassword());
        hikariConfig.setMaximumPoolSize(5);
        return new HikariDataSource(hikariConfig);
    }

    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(CommandParserFactory.class).to(CommandParserFactoryImpl.class).asEagerSingleton();
        bind(CustomCommandParserFactory.class).to(CustomCommandParserFactoryImpl.class).asEagerSingleton();
        bind(ViewInfoRepository.class).to(JsonViewInfoRepository.class).asEagerSingleton();
        bind(ConfigurationRepository.class).to(ConfigurationRepositoryImpl.class).asEagerSingleton();
        bind(UUIDGenerator.class).to(RandomUUIDGenerator.class).asEagerSingleton();
        bind(LabelService.class).to(LabelServiceImpl.class).asEagerSingleton();
        bind(OrderedLabelService.class).to(OrderedLabelServiceImpl.class).asEagerSingleton();
        bind(ViewInfoService.class).to(ViewInfoServiceImpl.class).asEagerSingleton();
        bind(PropertyDescriptorService.class).to(PropertyDescriptorServiceImpl.class).asEagerSingleton();
        bind(PropertyManager.class).asEagerSingleton();
        bind(TempIDManager.class).asEagerSingleton();
        bind(Executor.class).to(LocalExecutor.class);
        bind(LabelService.class).to(LabelServiceImpl.class).asEagerSingleton();
        bind(Context.class).to(TaskManagerContext.class);

        bind(File.class).annotatedWith(Names.named("propertyDescriptorJsonFile")).toInstance(OsDirs.getFile(OsDirs.DirType.DATA, taskManagerConfig.getProfile(),"property_descriptor.json"));
        bind(File.class).annotatedWith(Names.named("viewJsonFile")).toInstance(OsDirs.getFile(OsDirs.DirType.DATA, taskManagerConfig.getProfile(),"view.json"));
        bind(File.class).annotatedWith(Names.named("taskJsonFile")).toInstance(OsDirs.getFile(OsDirs.DirType.DATA, taskManagerConfig.getProfile(),"task.json"));
        bind(File.class).annotatedWith(Names.named("propertyToStringConverterJsonFile")).toInstance(OsDirs.getFile(OsDirs.DirType.DATA, taskManagerConfig.getProfile(), "property_to_string_converter.json"));
        bind(File.class).annotatedWith(Names.named("configurationYamlFile")).toInstance(OsDirs.getFile(OsDirs.DirType.CONFIG, taskManagerConfig.getProfile(),"config.yaml"));
    }

}

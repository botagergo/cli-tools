package cli_tools.task_manager.cli.functional_test;

import cli_tools.common.backend.ordered_label.repository.JsonOrderedLabelRepository;
import cli_tools.common.cli.Context;
import cli_tools.common.cli.command.custom_command.CustomCommandParserFactory;
import cli_tools.common.cli.command.custom_command.repository.CustomCommandRepository;
import cli_tools.common.cli.command.custom_command.repository.JsonCustomCommandRepository;
import cli_tools.common.cli.command_line.CommandLine;
import cli_tools.common.cli.executor.Executor;
import cli_tools.common.cli.executor.LocalExecutor;
import cli_tools.common.cli.command_line.JlineCommandLine;
import cli_tools.common.cli.command_parser.CommandParserFactory;
import cli_tools.common.cli.command_parser.CommandParserFactoryImpl;
import cli_tools.common.cli.tokenizer.Tokenizer;
import cli_tools.common.cli.tokenizer.TokenizerImpl;
import cli_tools.common.core.repository.*;
import cli_tools.common.backend.label.repository.JsonLabelRepository;
import cli_tools.common.backend.label.service.LabelService;
import cli_tools.common.backend.label.service.LabelServiceImpl;
import cli_tools.common.backend.ordered_label.service.OrderedLabelService;
import cli_tools.common.backend.ordered_label.service.OrderedLabelServiceImpl;
import cli_tools.common.backend.property_converter.PropertyConverter;
import cli_tools.common.backend.property_descriptor.repository.JsonPropertyDescriptorRepository;
import cli_tools.common.backend.property_descriptor.service.PropertyDescriptorService;
import cli_tools.common.backend.property_descriptor.service.PropertyDescriptorServiceImpl;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.backend.temp_id_mapping.TempIDManager;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.backend.view.repository.JsonViewInfoRepository;
import cli_tools.common.backend.view.service.ViewInfoService;
import cli_tools.common.backend.view.service.ViewInfoServiceImpl;
import cli_tools.task_manager.cli.TaskManagerContext;
import cli_tools.task_manager.cli.command.custom_command.CustomCommandDefinitionMixIn;
import cli_tools.task_manager.cli.command.custom_command.CustomCommandParserFactoryImpl;
import cli_tools.task_manager.backend.pseudo_property_provider.PseudoPropertyProviderMixIn;
import cli_tools.task_manager.backend.task.repository.JsonTaskRepository;
import cli_tools.task_manager.backend.task.service.TaskService;
import cli_tools.task_manager.backend.task.service.TaskServiceImpl;
import cli_tools.task_manager.cli.output_format.JsonOutputFormatRepository;
import cli_tools.task_manager.cli.output_format.OutputFormatRepository;
import cli_tools.test_utils.FileUtils;
import cli_tools.test_utils.RoundRobinUUIDGenerator;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;


public class TestModule extends AbstractModule {

    private final FileUtils fileUtils = new FileUtils();

    public TestModule() throws IOException {}

    @Provides
    @Singleton
    CustomCommandRepository customCommandRepository() {
        JsonCustomCommandRepository jsonCustomCommandRepository =
                new JsonCustomCommandRepository(fileUtils.getTempFile("property_descriptors"));
        jsonCustomCommandRepository.setMixIn(CustomCommandDefinitionMixIn.class);
        return jsonCustomCommandRepository;
    }

    @Provides
    @Singleton
    TaskService taskService(
            PropertyManager propertyManager,
            UUIDGenerator uuidGenerator,
            PropertyConverter propertyConverter) {
        return new TaskServiceImpl(
                new JsonTaskRepository(fileUtils.getTempFile("tasks"), uuidGenerator),
                new JsonTaskRepository(fileUtils.getTempFile("done_tasks"), uuidGenerator),
                propertyManager,
                uuidGenerator,
                propertyConverter);
    }

    @Provides
    @Singleton
    PropertyDescriptorRepository propertyDescriptorRepository(
            TempIDManager tempIdManager
    ) {
        return new JsonPropertyDescriptorRepository(
                fileUtils.getTempFile("property_descriptor"),
                tempIdManager,
                PseudoPropertyProviderMixIn.class);
    }

    @Provides
    @Singleton
    OutputFormatRepository outputFormatRepository() {
        return new JsonOutputFormatRepository(fileUtils.getTempFile("output_format"));
    }

    @Provides
    JlineCommandLine jlineCommandLine(Executor executor) {
        return new JlineCommandLine(executor, null, new File("history"));
    }

    @SneakyThrows
    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(CommandParserFactory.class).to(CommandParserFactoryImpl.class).asEagerSingleton();
        bind(CustomCommandParserFactory.class).to(CustomCommandParserFactoryImpl.class).asEagerSingleton();
        bind(ViewInfoRepository.class).to(JsonViewInfoRepository.class).asEagerSingleton();
        bind(LabelRepository.class).to(JsonLabelRepository.class).asEagerSingleton();
        bind(ConfigurationRepository.class).to(MockConfigurationRepository.class).asEagerSingleton();
        bind(UUIDGenerator.class).to(RoundRobinUUIDGenerator.class).asEagerSingleton();
        bind(LabelService.class).to(LabelServiceImpl.class).asEagerSingleton();
        bind(OrderedLabelRepository.class).to(JsonOrderedLabelRepository.class).asEagerSingleton();
        bind(OrderedLabelService.class).to(OrderedLabelServiceImpl.class).asEagerSingleton();
        bind(ViewInfoService.class).to(ViewInfoServiceImpl.class).asEagerSingleton();
        bind(PropertyDescriptorService.class).to(PropertyDescriptorServiceImpl.class).asEagerSingleton();
        bind(PropertyManager.class).asEagerSingleton();
        bind(TempIDManager.class).asEagerSingleton();
        bind(Executor.class).to(LocalExecutor.class);
        bind(LabelService.class).to(LabelServiceImpl.class).asEagerSingleton();
        bind(Context.class).to(TaskManagerContext.class);
        bind(CommandLine.class).to(JlineCommandLine.class);

        bind(File.class).annotatedWith(Names.named("propertyDescriptorJsonFile")).toInstance(fileUtils.getTempFile("property_descriptor"));
        bind(File.class).annotatedWith(Names.named("labelJsonFile")).toInstance(fileUtils.getTempFile("label"));
        bind(File.class).annotatedWith(Names.named("orderedLabelJsonFile")).toInstance(fileUtils.getTempFile("ordered_label"));
        bind(File.class).annotatedWith(Names.named("viewJsonFile")).toInstance(fileUtils.getTempFile("view"));
        bind(File.class).annotatedWith(Names.named("propertyToStringConverterJsonFile")).toInstance(fileUtils.getTempFile("property_to_string_converter"));
        bind(File.class).annotatedWith(Names.named("configurationYamlFile")).toInstance(fileUtils.getTempFile("config"));
    }

}

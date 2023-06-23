package task_manager.ui.cli;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import task_manager.logic.use_case.label.LabelUseCase;
import task_manager.logic.use_case.label.LabelUseCaseImpl;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCase;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCaseImpl;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCase;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCaseImpl;
import task_manager.logic.use_case.status.StatusUseCase;
import task_manager.logic.use_case.status.StatusUseCaseImpl;
import task_manager.logic.use_case.tag.TagUseCase;
import task_manager.logic.use_case.tag.TagUseCaseImpl;
import task_manager.logic.use_case.task.TaskUseCase;
import task_manager.logic.use_case.task.TaskUseCaseImpl;
import task_manager.logic.use_case.view.ViewUseCase;
import task_manager.logic.use_case.view.ViewUseCaseImpl;
import task_manager.repository.*;
import task_manager.repository.label.JsonLabelRepositoryFactory;
import task_manager.repository.ordered_label.JsonOrderedLabelRepositoryFactory;
import task_manager.repository.property_descriptor.JsonPropertyDescriptorRepository;
import task_manager.repository.task.JsonTaskRepository;
import task_manager.repository.temp_id_mapping.JsonTempIDMappingRepository;
import task_manager.repository.view.JsonViewInfoRepository;
import task_manager.ui.cli.command_line.CommandLine;
import task_manager.ui.cli.command_line.Executor;
import task_manager.ui.cli.command_line.ExecutorImpl;
import task_manager.ui.cli.command_line.JlineCommandLine;
import task_manager.ui.cli.command_parser.CommandParserFactory;
import task_manager.ui.cli.command_parser.CommandParserFactoryImpl;
import task_manager.ui.cli.tokenizer.Tokenizer;
import task_manager.ui.cli.tokenizer.TokenizerImpl;
import task_manager.util.RandomUUIDGenerator;
import task_manager.util.UUIDGenerator;

import java.io.File;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(TaskRepository.class).to(JsonTaskRepository.class);
        bind(ViewInfoRepository.class).to(JsonViewInfoRepository.class);
        bind(LabelRepositoryFactory.class).to(JsonLabelRepositoryFactory.class);
        bind(OrderedLabelRepositoryFactory.class).to(JsonOrderedLabelRepositoryFactory.class);
        bind(PropertyDescriptorRepository.class).to(JsonPropertyDescriptorRepository.class);
        bind(UUIDGenerator.class).to(RandomUUIDGenerator.class);
        bind(TaskUseCase.class).to(TaskUseCaseImpl.class);
        bind(TagUseCase.class).to(TagUseCaseImpl.class);
        bind(StatusUseCase.class).to(StatusUseCaseImpl.class);
        bind(LabelUseCase.class).to(LabelUseCaseImpl.class);
        bind(OrderedLabelUseCase.class).to(OrderedLabelUseCaseImpl.class);
        bind(ViewUseCase.class).to(ViewUseCaseImpl.class);
        bind(PropertyDescriptorUseCase.class).to(PropertyDescriptorUseCaseImpl.class);
        bind(TempIDMappingRepository.class).to(JsonTempIDMappingRepository.class);
        bind(CommandParserFactory.class).to(CommandParserFactoryImpl.class);
        bind(CommandLine.class).to(JlineCommandLine.class);
        bind(Executor.class).to(ExecutorImpl.class);
        bind(LabelUseCase.class).to(LabelUseCaseImpl.class);
        bind(File.class).annotatedWith(Names.named("taskJsonFile")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/task.json"));
        bind(File.class).annotatedWith(Names.named("tempIdMappingJsonFile")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/temp_id_mapping.json"));
        bind(File.class).annotatedWith(Names.named("propertyDescriptorJsonFile")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/property_descriptor.json"));
        bind(File.class).annotatedWith(Names.named("viewInfoJsonFile")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/view_info.json"));
        bind(File.class).annotatedWith(Names.named("basePath")).toInstance(new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

}

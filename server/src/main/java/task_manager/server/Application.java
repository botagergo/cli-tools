package task_manager.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import task_manager.init.Initializer;
import task_manager.logic.use_case.ordered_label.OrderedLabelUseCaseImpl;
import task_manager.logic.use_case.property_descriptor.PropertyDescriptorUseCaseImpl;
import task_manager.logic.use_case.status.StatusUseCaseImpl;
import task_manager.logic.use_case.tag.TagUseCaseImpl;
import task_manager.logic.use_case.task.TaskUseCaseImpl;
import task_manager.logic.use_case.view.PropertyConverter;
import task_manager.logic.use_case.view.ViewUseCaseImpl;
import task_manager.property.PropertyManager;
import task_manager.repository.label.JsonLabelRepository;
import task_manager.repository.ordered_label.JsonOrderedLabelRepositoryFactory;
import task_manager.repository.view.JsonViewInfoRepository;
import task_manager.server.repository.MongoLabelRepositoryFactory;
import task_manager.server.repository.MongoPropertyDescriptorRepository;
import task_manager.server.repository.MongoTaskRepository;
import task_manager.util.RandomUUIDGenerator;
import task_manager.util.UUIDGenerator;

import java.io.File;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    @Bean
    MongoTaskRepository taskRepository() {
        return new MongoTaskRepository(mongoClient(), MONGODB_DATABASE_NAME,
            MONGODB_TASK_COLLECTION_NAME);
    }

    @Bean
    MongoLabelRepositoryFactory labelRepositoryFactory() {
        return new MongoLabelRepositoryFactory(mongoClient(), MONGODB_DATABASE_NAME);
    }

    @Bean
    JsonLabelRepository statusRepository() {
        return new JsonLabelRepository(new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

    @Bean
    JsonOrderedLabelRepositoryFactory orderedLabelRepositoryFactory() {
        return new JsonOrderedLabelRepositoryFactory(new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

    @Bean
    JsonViewInfoRepository viewInfoRepository() {
        throw new NotImplementedException();
    }

    @Bean
    TaskUseCaseImpl taskUseCase() {
        return new TaskUseCaseImpl(taskRepository(), viewUseCase(), propertyManager(), new RandomUUIDGenerator());
    }

    @Bean
    TagUseCaseImpl tagUseCase() {
        return new TagUseCaseImpl(labelRepositoryFactory(), new RandomUUIDGenerator());
    }

    @Bean
    StatusUseCaseImpl statusUseCase() {
        return new StatusUseCaseImpl(labelRepositoryFactory(), uuidGenerator());
    }

    @Bean
    OrderedLabelUseCaseImpl orderedLabelUseCase() {
        return new OrderedLabelUseCaseImpl(orderedLabelRepositoryFactory());
    }

    @Bean
    ViewUseCaseImpl viewUseCase() {
        return new ViewUseCaseImpl(viewInfoRepository(), new PropertyConverter(labelRepositoryFactory()));
    }

    @Bean
    MongoPropertyDescriptorRepository propertyDescriptorRepository() {
        return new MongoPropertyDescriptorRepository(mongoClient(), MONGODB_DATABASE_NAME,
                MONGODB_PROPERTY_DESCRIPTOR_COLLECTION_NAME);
    }

    @Bean
    PropertyDescriptorUseCaseImpl propertyDescriptorUseCase() {
        return new PropertyDescriptorUseCaseImpl(
            propertyDescriptorRepository());
    }

    @Bean
    MongoClient mongoClient() {
        return MongoClients.create("mongodb://" + MONGODB_HOST + ":" + MONGODB_PORT);
    }

    @Bean
    UUIDGenerator uuidGenerator() {
        return new RandomUUIDGenerator();
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(64000);
        return loggingFilter;
    }

    @Bean
    public Initializer initializer() {
        return new Initializer(orderedLabelUseCase(), propertyDescriptorUseCase(), statusUseCase());
    }

    @Bean
    public PropertyManager propertyManager() {
        return new PropertyManager(propertyDescriptorRepository());
    }

    @Value("${mongodb.host}")
    private String MONGODB_HOST;

    @Value("${mongodb.port}")
    private String MONGODB_PORT;

    @Value("${mongodb.database_name}")
    private String MONGODB_DATABASE_NAME;

    @Value("${mongodb.task_collection_name}")
    private String MONGODB_TASK_COLLECTION_NAME;

    @Value("${mongodb.property_descriptor_collection_name}")
    private String MONGODB_PROPERTY_DESCRIPTOR_COLLECTION_NAME;

}

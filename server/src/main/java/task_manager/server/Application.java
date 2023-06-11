package task_manager.server;

import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import task_manager.property.PropertyManager;
import task_manager.init.Initializer;
import task_manager.logic.use_case.PropertyDescriptorUseCaseImpl;
import task_manager.logic.use_case.StatusUseCaseImpl;
import task_manager.logic.use_case.TagUseCaseImpl;
import task_manager.logic.use_case.TaskUseCaseImpl;
import task_manager.repository.*;
import task_manager.server.repository.MongoLabelRepositoryFactory;
import task_manager.server.repository.MongoPropertyDescriptorRepository;
import task_manager.server.repository.MongoTaskRepository;
import task_manager.util.RandomUUIDGenerator;
import task_manager.util.UUIDGenerator;

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
    TaskUseCaseImpl taskUseCase() {
        return new TaskUseCaseImpl(taskRepository(), propertyManager(), new RandomUUIDGenerator());
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
        return new Initializer(propertyDescriptorUseCase(), statusUseCase());
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

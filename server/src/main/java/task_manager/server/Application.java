package task_manager.server;

import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import task_manager.annotation.StatusAnnotation;
import task_manager.annotation.TagAnnotation;
import task_manager.init.Initializer;
import task_manager.logic.use_case.PropertyDescriptorUseCase;
import task_manager.logic.use_case.StatusUseCase;
import task_manager.logic.use_case.TagUseCase;
import task_manager.logic.use_case.TaskUseCase;
import task_manager.repository.*;
import task_manager.server.repository.MongoLabelRepository;
import task_manager.server.repository.MongoPropertyDescriptorRepository;
import task_manager.server.repository.MongoTaskRepository;

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
    @TagAnnotation
    MongoLabelRepository tagRepository() {
        return new MongoLabelRepository(mongoClient(), MONGODB_DATABASE_NAME,
                MONGODB_TAG_COLLECTION_NAME);
    }

    @Bean
    @StatusAnnotation
    JsonLabelRepository statusRepository() {
        return new JsonLabelRepository("statuses",
            new File(System.getProperty("user.home") + "/.config/task_manager/"));
    }

    @Bean
    TaskUseCase taskUseCase() {
        return new TaskUseCase(taskRepository());
    }

    @Bean
    TagUseCase tagUseCase() {
        return new TagUseCase(tagRepository());
    }

    @Bean
    StatusUseCase statusUseCase() {
        return new StatusUseCase(statusRepository());
    }

    @Bean
    MongoPropertyDescriptorRepository propertyDescriptorRepository() {
        return new MongoPropertyDescriptorRepository(mongoClient(), MONGODB_DATABASE_NAME,
            MONGODB_PROPERTY_DESCRIPTOR_COLLECTION_NAME);
    }

    @Bean
    PropertyDescriptorUseCase propertyDescriptorUseCase() {
        return new PropertyDescriptorUseCase(
            propertyDescriptorRepository());
    }

    @Bean
    MongoClient mongoClient() {
        return MongoClients.create("mongodb://" + MONGODB_HOST + ":" + MONGODB_PORT);
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
    Initializer initializer() {
        return new Initializer(propertyDescriptorRepository(), statusRepository());
    }

    @Value("${mongodb.host}")
    private String MONGODB_HOST;

    @Value("${mongodb.port}")
    private String MONGODB_PORT;

    @Value("${mongodb.database_name}")
    private String MONGODB_DATABASE_NAME;

    @Value("${mongodb.task_collection_name}")
    private String MONGODB_TASK_COLLECTION_NAME;

    @Value("${mongodb.tag_collection_name}")
    private String MONGODB_TAG_COLLECTION_NAME;

    @Value("${mongodb.property_descriptor_collection_name}")
    private String MONGODB_PROPERTY_DESCRIPTOR_COLLECTION_NAME;

}

package common.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import common.init.Initializer;
import common.task_logic.service.label.LabelUseCaseImpl;
import common.task_logic.service.ordered_label.OrderedLabelUseCaseImpl;
import common.task_logic.service.property_descriptor.PropertyDescriptorUseCaseImpl;
import common.task_logic.service.task.TaskUseCaseImpl;
import common.task_logic.service.view.ViewInfoUseCaseImpl;
import common.repository.label.JsonLabelRepository;
import common.repository.ordered_label.JsonOrderedLabelRepositoryFactory;
import common.repository.view.JsonViewInfoRepository;
import common.server.repository.MongoLabelRepositoryFactory;
import common.server.repository.MongoPropertyDescriptorRepository;
import common.server.repository.MongoTaskRepository;

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
        return new JsonLabelRepository(new File(System.getProperty("user.home") + "/.config/common/"));
    }

    @Bean
    JsonOrderedLabelRepositoryFactory orderedLabelRepositoryFactory() {
        return new JsonOrderedLabelRepositoryFactory(new File(System.getProperty("user.home") + "/.config/common/"));
    }

    @Bean
    JsonViewInfoRepository viewInfoRepository() {
        throw new NotImplementedException();
    }

    @Bean
    TaskUseCaseImpl taskUseCase() {
        throw new NotImplementedException();
    }

    @Bean
    LabelUseCaseImpl labelUseCase() {
        return new LabelUseCaseImpl(labelRepositoryFactory(), uuidGenerator());
    }

    @Bean
    OrderedLabelUseCaseImpl orderedLabelUseCase() {
        return new OrderedLabelUseCaseImpl(orderedLabelRepositoryFactory());
    }

    @Bean
    ViewInfoUseCaseImpl viewUseCase() {
        return new ViewInfoUseCaseImpl(viewInfoRepository());
    }

    @Bean
    MongoPropertyDescriptorRepository propertyDescriptorRepository() {
        return new MongoPropertyDescriptorRepository(mongoClient(), MONGODB_DATABASE_NAME,
                MONGODB_PROPERTY_DESCRIPTOR_COLLECTION_NAME);
    }

    @Bean
    PropertyDescriptorUseCaseImpl propertyDescriptorUseCase() {
        throw new NotImplementedException();
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
        return new Initializer(propertyDescriptorUseCase(), labelUseCase(), orderedLabelUseCase());
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

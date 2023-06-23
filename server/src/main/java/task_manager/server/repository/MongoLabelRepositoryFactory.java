package task_manager.server.repository;

import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import task_manager.core.repository.LabelRepository;
import task_manager.core.repository.LabelRepositoryFactory;

import javax.inject.Inject;

public class MongoLabelRepositoryFactory implements LabelRepositoryFactory {

    @Inject
    public MongoLabelRepositoryFactory(MongoClient mongoClient,
                                       @Value("mongodb.database_name") String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    @Override
    public LabelRepository getLabelRepository(String labelName) {
        return new MongoLabelRepository(mongoClient, databaseName, labelName);
    }

    private final MongoClient mongoClient;

    private final String databaseName;

}

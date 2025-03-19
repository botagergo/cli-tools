package common.server.repository;

import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import common.core.repository.LabelRepository;
import common.core.repository.LabelRepositoryFactory;

import javax.inject.Inject;

public class MongoLabelRepositoryFactory implements LabelRepositoryFactory {

    @Inject
    public MongoLabelRepositoryFactory(MongoClient mongoClient,
                                       @Value("mongodb.database_name") String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    @Override
    public LabelRepository getLabelRepository(String labelType) {
        return new MongoLabelRepository(mongoClient, databaseName, labelType);
    }

    private final MongoClient mongoClient;

    private final String databaseName;

}

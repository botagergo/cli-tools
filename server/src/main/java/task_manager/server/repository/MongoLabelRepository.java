package task_manager.server.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import task_manager.core.data.Label;
import task_manager.core.repository.LabelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoLabelRepository implements LabelRepository {

    public MongoLabelRepository(MongoClient mongoClient, String databaseName,
        String labelName) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        mongoCollection = mongoDatabase.getCollection(labelName);
    }

    @Override
    public Label create(Label label) {
        mongoCollection.insertOne(labelToDocument(label));
        return label;
    }

    @Override
    public Label get(UUID uuid) {
        Document document = mongoCollection.find(new Document("uuid", uuid.toString())).first();
        return documentToLabel(document);
    }

    @Override
    public List<Label> getAll() {
        ArrayList<Label> labels = new ArrayList<>();
        return mongoCollection.find().map(this::documentToLabel).into(labels);
    }

    @Override
    public Label find(String name) {
        Document document = mongoCollection.find(new Document("name", name)).first();
        return documentToLabel(document);
    }

    @Override
    public Label update(Label label) {
        Document document = mongoCollection.find(new Document("uuid", label.uuid())).first();
        if (document == null) {
            return null;
        }

        mongoCollection.replaceOne(new Document("name", label.text()),
                document);
        return documentToLabel(document);
    }

    @Override
    public boolean delete(UUID uuid) {
        return mongoCollection.deleteOne(new Document("uuid", uuid.toString()))
            .getDeletedCount() > 0;
    }

    @Override
    public void deleteAll() {
        mongoCollection.deleteMany(new Document());
    }

    private Document labelToDocument(Label label) {
        Document document = new Document();
        document.append("uuid", label.uuid().toString());
        document.append("name", label.text());
        return document;
    }

    private Label documentToLabel(Document document) {
        if (document == null) {
            return null;
        }
        return new Label(UUID.fromString(document.getString("uuid")), document.getString("name"));
    }

    private final MongoCollection<Document> mongoCollection;

}

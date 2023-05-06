package task_manager.server.repository;

import task_manager.data.Task;
import task_manager.data.property.Property;
import task_manager.data.property.PropertyException;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.util.*;

import task_manager.repository.TaskRepository;

public class MongoTaskRepository implements TaskRepository {

    public MongoTaskRepository(MongoClient mongoClient, String databaseName,
        String taskCollectionName) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        mongoCollection = mongoDatabase.getCollection(taskCollectionName);
    }

    @Override
    public Task create(Task task) {
        mongoCollection.insertOne(taskToDocument(task));
        return task;
    }

    @Override
    public Task get(UUID uuid) {
        Document document = mongoCollection.find(new Document("uuid", uuid.toString())).first();
        return documentToTask(document);
    }

    @Override
    public List<Task> getAll() {
        ArrayList<Task> tasks = new ArrayList<>();
        return mongoCollection.find().map(this::documentToTask).into(tasks);
    }

    @Override
    public Task update(Task task) throws IOException {
        try {
            Document filter = new Document("uuid", task.getUuid().toString());
            Document document =
                mongoCollection.find(filter).first();
            if (document == null) {
                return null;
            }

            for (Pair<String, Property> pair : task.getPropertiesIter()) {
                if (!Objects.equals(pair.getKey(), "uuid")) {
                    document.put(pair.getKey(), pair.getValue().getRawValue());
                }
            }
            mongoCollection.replaceOne(filter, document);
            return documentToTask(document);
        } catch (PropertyException e) {
            throw new IOException();
        }
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

    private Document taskToDocument(Task task) {
        Map<String, Object> map = task.asMap();
        Document document = new Document();
        for (String key : map.keySet()) {
            document.append(key, map.get(key));
        }
        return document;
    }

    private Task documentToTask(Document document) {
        if (document == null) {
            return null;
        }

        HashMap<String, Object> map = new HashMap<>();
        for (String key : document.keySet()) {
            if (!key.equals("_id")) {
                map.put(key, document.get(key));
            }
        }
        return Task.fromMap(map);
    }

    private final MongoCollection<Document> mongoCollection;

}

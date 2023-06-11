package task_manager.server.repository;

import org.apache.commons.lang3.NotImplementedException;
import task_manager.property.PropertyDescriptor;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import task_manager.repository.PropertyDescriptorRepository;

import java.util.ArrayList;
import java.util.List;

public class MongoPropertyDescriptorRepository implements PropertyDescriptorRepository {

    public MongoPropertyDescriptorRepository(MongoClient mongoClient, String databaseName,
        String taskCollectionName) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        mongoCollection = mongoDatabase.getCollection(taskCollectionName);
    }

    @Override
    public PropertyDescriptor get(String name) {
        throw new NotImplementedException();
    }

    public List<PropertyDescriptor> getAll() {
        List<PropertyDescriptor> propertyDescriptorCollection =
            new ArrayList<>();
        mongoCollection.find().map(this::documentToPropertyDescriptor)
            .forEach(propertyDescriptorCollection::add);
        return propertyDescriptorCollection;
    }

    @Override
    public void create(PropertyDescriptor propertyDescriptor) {
        mongoCollection.insertOne(propertyDescriptorToDocument(propertyDescriptor));
    }

    private PropertyDescriptor documentToPropertyDescriptor(Document document) {
        if (document == null) {
            return null;
        }

        PropertyDescriptor.Type type = null;
        String typeStr = document.getString("type");
        switch (typeStr) {
            case "String" -> type = PropertyDescriptor.Type.String;
            case "Boolean" -> type = PropertyDescriptor.Type.Boolean;
            case "UUID" -> type = PropertyDescriptor.Type.UUID;
        }

        return new PropertyDescriptor(
            document.getString("name"),
            type,
            PropertyDescriptor.Multiplicity.valueOf(document.getString("multiplicity")),
            document.get("defaultValue"));
    }

    private Document propertyDescriptorToDocument(PropertyDescriptor propertyDescriptor) {
        Document document = new Document();
        document.append("name", propertyDescriptor.name());
        document.append("type", propertyDescriptor.type().toString());
        document.append("multiplicity", propertyDescriptor.multiplicity().toString());
        document.append("defaultValue", propertyDescriptor.defaultValue());
        return document;
    }

    private final MongoCollection<Document> mongoCollection;

}

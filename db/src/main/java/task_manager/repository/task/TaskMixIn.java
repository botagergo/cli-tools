package task_manager.repository.task;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import task_manager.property.PropertyOwner;
import task_manager.repository.MapDeserializer;
import task_manager.repository.MapSerializer;

import java.util.HashMap;

@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE)
public abstract class TaskMixIn implements PropertyOwner {

    @JsonSetter
    @JsonGetter
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    public abstract HashMap<String, Object> getProperties();

}

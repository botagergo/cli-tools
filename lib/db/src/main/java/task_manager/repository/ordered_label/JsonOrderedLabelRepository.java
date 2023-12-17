package task_manager.repository.ordered_label;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import task_manager.core.data.OrderedLabel;
import task_manager.core.repository.OrderedLabelRepository;
import task_manager.repository.JsonRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JsonOrderedLabelRepository extends JsonRepository<ArrayList<String>, ArrayList<OrderedLabel>> implements OrderedLabelRepository {

    public JsonOrderedLabelRepository(File jsonFile) {
        super(jsonFile);
        getObjectMapper().addMixIn(OrderedLabelMixIn.class, OrderedLabelMixIn.class);
    }

    @Override
    public OrderedLabel find(String text) throws IOException {
        return getData().stream().filter(t -> t.text().equals(text))
                .findAny().orElse(null);
    }

    @Override
    public OrderedLabel get(int value) throws IOException {
        ArrayList<OrderedLabel> orderedLabels = getData();
        if (value < 0 || value >= orderedLabels.size()) {
            return null;
        }
        return orderedLabels.get(value);
    }

    @Override
    public List<OrderedLabel> getAll() throws IOException {
        return getData();
    }

    @Override
    public OrderedLabel create(String text) throws IOException {
        ArrayList<OrderedLabel> data = getData();
        OrderedLabel orderedLabel;
        if (data.isEmpty()) {
            orderedLabel = new OrderedLabel(text, 0);
        } else {
            orderedLabel = new OrderedLabel(text, data.get(data.size() - 1).value());
        }
        data.add(orderedLabel);
        writeData();
        return orderedLabel;
    }

    @Override
    public ArrayList<String> getEmptyData() {
        return new ArrayList<>();
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(ArrayList.class, String.class);
    }

    @Override
    protected ArrayList<OrderedLabel> jsonToStoredData(ArrayList<String> data) {
        return IntStream.range(0, data.size())
                .mapToObj(i -> new OrderedLabel(data.get(i), i)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    protected ArrayList<String> storedToJsonData(ArrayList<OrderedLabel> data) {
        return data.stream().map(OrderedLabel::text).collect(Collectors.toCollection(ArrayList::new));
    }
}

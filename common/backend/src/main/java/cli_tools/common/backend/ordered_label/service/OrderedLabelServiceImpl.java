package cli_tools.common.backend.ordered_label.service;

import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.core.repository.OrderedLabelRepository;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class OrderedLabelServiceImpl implements OrderedLabelService {

    private final OrderedLabelRepository orderedLabelRepository;

    @Override
    public OrderedLabel getOrderedLabel(String labelType, int labelValue) throws IOException {
        return new OrderedLabel(orderedLabelRepository.get(labelType, labelValue), labelValue);
    }

    @Override
    public OrderedLabel findOrderedLabel(String labelType, String labelText) throws IOException {
        return new OrderedLabel(labelText, orderedLabelRepository.find(labelType, labelText));
    }

    @Override
    public void createOrderedLabel(String labelType, String labelText) throws IOException {
        orderedLabelRepository.create(labelType, labelText);
    }

    @Override
    public List<OrderedLabel> getOrderedLabels(String labelType) throws IOException {
        var labels = orderedLabelRepository.getAll(labelType);
        return IntStream.range(0, labels.size()).mapToObj(i -> new OrderedLabel(labels.get(i), i)).toList();
    }

    @Override
    public void deleteAllOrderedLabels(String labelType) throws IOException {
        orderedLabelRepository.deleteAll(labelType);
    }

}

package cli_tools.common.backend.ordered_label.service;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.core.repository.ConstraintViolationException;
import cli_tools.common.core.repository.OrderedLabelRepository;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.stream.IntStream;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class OrderedLabelServiceImpl implements OrderedLabelService {

    private final OrderedLabelRepository orderedLabelRepository;

    @Override
    public OrderedLabel getOrderedLabel(@NonNull String orderedLabelType, int orderedLabelValue) {
        String orderedLabelText = orderedLabelRepository.get(orderedLabelType, orderedLabelValue);
        if (orderedLabelText == null) {
            return null;
        }
        return new OrderedLabel(orderedLabelText, orderedLabelValue);
    }

    @Override
    public OrderedLabel findOrderedLabel(@NonNull String labelType, @NonNull String labelText) {
        Integer orderedLabelValue = orderedLabelRepository.find(labelType, labelText);
        if (orderedLabelValue == null) {
            return null;
        }
        return new OrderedLabel(labelText, orderedLabelValue);
    }

    @Override
    public void createOrderedLabel(@NonNull String labelType, @NonNull String labelText, int labelValue) throws ServiceException {
        try {
            orderedLabelRepository.create(labelType, labelText, labelValue);
        } catch (ConstraintViolationException e) {
            throw new ServiceException("Ordered label already exists: %s (%s)".formatted(labelType, labelText));
        }
    }

    @Override
    public @NonNull List<OrderedLabel> getOrderedLabels(@NonNull String labelType) {
        var labels = orderedLabelRepository.getAll(labelType);
        return IntStream.range(0, labels.size()).mapToObj(i -> new OrderedLabel(labels.get(i), i)).toList();
    }

    @Override
    public void deleteAllOrderedLabels(@NonNull String labelType) {
        orderedLabelRepository.deleteAll(labelType);
    }

}

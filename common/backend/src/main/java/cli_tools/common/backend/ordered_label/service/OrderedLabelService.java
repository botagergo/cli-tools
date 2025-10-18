package cli_tools.common.backend.ordered_label.service;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.OrderedLabel;
import lombok.NonNull;

import java.util.List;

public interface OrderedLabelService {
    OrderedLabel getOrderedLabel(@NonNull String labelType, int labelValue) throws ServiceException;

    OrderedLabel findOrderedLabel(@NonNull String labelType, @NonNull String labelText) throws ServiceException;

    void createOrderedLabel(@NonNull String labelType, @NonNull String labelText, int labelValue) throws ServiceException;

    @NonNull List<OrderedLabel> getOrderedLabels(@NonNull String labelType) throws ServiceException;

    void deleteAllOrderedLabels(@NonNull String labelType) throws ServiceException;
}

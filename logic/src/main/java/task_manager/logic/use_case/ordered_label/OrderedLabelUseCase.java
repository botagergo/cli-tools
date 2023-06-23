package task_manager.logic.use_case.ordered_label;

import task_manager.data.OrderedLabel;

import java.io.IOException;

public interface OrderedLabelUseCase {
    OrderedLabel getOrderedLabel(String labelName, int labelValue) throws IOException;

    void createOrderedLabel(String labelName, String labelText) throws IOException;
}

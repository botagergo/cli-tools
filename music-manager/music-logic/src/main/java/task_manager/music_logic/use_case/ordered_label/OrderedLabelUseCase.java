package common.music_logic.use_case.ordered_label;

import common.core.data.OrderedLabel;

import java.io.IOException;

public interface OrderedLabelUseCase {
    OrderedLabel getOrderedLabel(String labelName, int labelValue) throws IOException;

    OrderedLabel findOrderedLabel(String labelType, String labelText) throws IOException;

    void createOrderedLabel(String labelName, String labelText) throws IOException;
}

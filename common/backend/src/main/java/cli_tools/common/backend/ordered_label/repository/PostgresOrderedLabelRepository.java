package cli_tools.common.backend.ordered_label.repository;

import cli_tools.common.backend.repository.PostgresRepository;
import cli_tools.common.core.repository.ConstraintViolationException;
import cli_tools.common.core.repository.OrderedLabelRepository;
import cli_tools.common.db_schema.tables.records.OrderedLabelsRecord;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jooq.exception.IntegrityConstraintViolationException;

import javax.sql.DataSource;
import java.util.List;

import static cli_tools.common.db_schema.Tables.ORDERED_LABELS;


@Log4j2
public class PostgresOrderedLabelRepository extends PostgresRepository implements OrderedLabelRepository {

    public PostgresOrderedLabelRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(@NonNull String type, @NonNull String text, int value) {
        try {
            return ctx().insertInto(ORDERED_LABELS)
                    .set(ORDERED_LABELS.LABEL_TYPE, type)
                    .set(ORDERED_LABELS.LABEL_TEXT, text)
                    .set(ORDERED_LABELS.LABEL_VALUE, value)
                    .execute() > 1;
        } catch (IntegrityConstraintViolationException e) {
            throw new ConstraintViolationException("Ordered label '%s' (%s) already exists".formatted(text, type), e);
        }
    }

    @Override
    public String get(@NonNull String type, int value) {
        return ctx().selectFrom(ORDERED_LABELS)
                .where(ORDERED_LABELS.LABEL_TYPE.eq(type))
                .and(ORDERED_LABELS.LABEL_VALUE.eq(value))
                .fetchOne(ORDERED_LABELS.LABEL_TEXT);
    }

    @Override
    public @NonNull List<String> getAll(@NonNull String type) {
        return ctx().selectFrom(ORDERED_LABELS).where(ORDERED_LABELS.LABEL_TYPE.eq(type))
                .stream().map(OrderedLabelsRecord::getLabelText)
                .toList();
    }

    @Override
    public Integer find(@NonNull String type, @NonNull String text) {
        return ctx().selectFrom(ORDERED_LABELS)
                .where(ORDERED_LABELS.LABEL_TYPE.eq(type))
                .and(ORDERED_LABELS.LABEL_TEXT.eq(text))
                .fetchOne(ORDERED_LABELS.LABEL_VALUE);
    }

    @Override
    public void deleteAll(@NonNull String labelType) {
        ctx().deleteFrom(ORDERED_LABELS)
                .where(ORDERED_LABELS.LABEL_TYPE.eq(labelType))
                .execute();
    }
}

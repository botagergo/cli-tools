package cli_tools.common.backend.label.repository;

import cli_tools.common.backend.repository.PostgresRepository;
import cli_tools.common.core.repository.ConstraintViolationException;
import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.core.repository.LabelRepository;
import cli_tools.common.db_schema.tables.records.LabelsRecord;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jooq.exception.IntegrityConstraintViolationException;

import javax.sql.DataSource;
import java.util.*;

import static cli_tools.common.db_schema.Tables.LABELS;


@Log4j2
public class PostgresLabelRepository extends PostgresRepository implements LabelRepository {

    public PostgresLabelRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public @NonNull UUID create(@NonNull String labelType, @NonNull String labelText) throws DataAccessException {
        try {
            return ctx().insertInto(LABELS)
                    .set(LABELS.LABEL_TYPE, labelType)
                    .set(LABELS.LABEL_TEXT, labelText)
                    .returning(LABELS.UUID)
                    .fetchSingle().getUuid();
        } catch (IntegrityConstraintViolationException e) {
            throw new ConstraintViolationException("Label '%s' (%s) already exists".formatted(labelText, labelType), e);
        }
    }

    @Override
    public String get(@NonNull String labelType, @NonNull UUID uuid) {
        return ctx().selectFrom(LABELS)
                .where(LABELS.LABEL_TYPE.eq(labelType))
                .and(LABELS.UUID.eq(uuid))
                .fetchOne(LABELS.LABEL_TEXT);
    }

    @Override
    public UUID find(@NonNull String labelType, @NonNull String labelText) {
        return ctx().selectFrom(LABELS)
                .where(LABELS.LABEL_TYPE.eq(labelType))
                .and(LABELS.LABEL_TEXT.eq(labelText))
                .fetchOne(LABELS.UUID);
    }

    @Override
    public @NonNull List<String> getAllWithType(@NonNull String labelType) {
        return ctx().selectFrom(LABELS)
                .where(LABELS.LABEL_TYPE.eq(labelType))
                .stream().map(LabelsRecord::getLabelText)
                .toList();
    }

    @Override
    public @NonNull LinkedHashMap<String, LinkedHashMap<UUID, String>> getAll() {
        LinkedHashMap<String, LinkedHashMap<UUID, String>> labels = new LinkedHashMap<>();
        ctx().selectFrom(LABELS)
                .forEach(record ->
                        labels.computeIfAbsent(record.getLabelType(), key -> new LinkedHashMap<>())
                                .put(record.getUuid(), record.getLabelText()));
        return labels;
    }

    @Override
    public boolean delete(@NonNull String labelType, @NonNull UUID uuid) {
        return 0 < ctx().deleteFrom(LABELS)
                .where(LABELS.LABEL_TYPE.eq(labelType))
                .and(LABELS.UUID.eq(uuid))
                .execute();
    }

    @Override
    public void deleteAll(@NonNull String labelType) {
        ctx().deleteFrom(LABELS)
                .where(LABELS.LABEL_TYPE.eq(labelType))
                .execute();
    }
}

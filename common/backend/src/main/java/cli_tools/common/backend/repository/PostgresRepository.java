package cli_tools.common.backend.repository;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import javax.sql.DataSource;

public abstract class PostgresRepository {

    private final DataSource dataSource;
    private DSLContext dslContext;

    public PostgresRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected DSLContext ctx() {
        if (dslContext == null) {
            Settings settings = new Settings()
                    .withRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED);
            dslContext = DSL.using(dataSource, SQLDialect.POSTGRES, settings);
        }
        return dslContext;
    }

}

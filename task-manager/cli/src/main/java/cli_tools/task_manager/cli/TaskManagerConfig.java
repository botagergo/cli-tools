package cli_tools.task_manager.cli;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class TaskManagerConfig {
    private String profile;
    String postgresqlUrl;
    String postgresqlUsername;
    String postgresqlPassword;
    DatabaseMode databaseMode;

    public enum DatabaseMode {
        JSON,
        POSTGRESQL
    }
}

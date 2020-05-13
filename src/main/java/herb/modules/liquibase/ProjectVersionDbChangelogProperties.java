package herb.modules.liquibase;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectVersionDbChangelogProperties {

    private String version;

    private List<DatabaseChangeLogProp> changeLogs = new ArrayList<>();

    @Data
    public static class DatabaseChangeLogProp{
        private String database;
        private String changeLog;
    }
}

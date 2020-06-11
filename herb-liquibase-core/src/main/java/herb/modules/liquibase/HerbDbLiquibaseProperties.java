package herb.modules.liquibase;

import lombok.Data;



@Data
public class HerbDbLiquibaseProperties {

    private String driver;

    private String url;

    private String username;

    private String password;

//    private String changeLog;

    private String databaseChangeLogTable;

    private String databaseChangeLogLockTable;

}

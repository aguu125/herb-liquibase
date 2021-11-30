package herb.modules.liquibase.config;

import lombok.Data;


/**
 * 数据库连接相关配置
 */
@Data
public class DataSourceProperties {
    /**
     * driver
     */
    private String driver;
    /**
     * url
     */
    private String url;
    /**
     * 连接用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

//    private String changeLog;

    //private String databaseChangeLogTable;

    //private String databaseChangeLogLockTable;
}

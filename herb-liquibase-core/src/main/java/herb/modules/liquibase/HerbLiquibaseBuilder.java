package herb.modules.liquibase;

import herb.modules.liquibase.config.DataSourceProperties;
import herb.modules.liquibase.config.ProjectVersionsProperties.DatabaseVersionProperties;
import herb.modules.liquibase.core.HerbLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;


@Component
@Slf4j
public class HerbLiquibaseBuilder implements ResourceLoaderAware {

//    private HerbLiquibaseProperties liquibaseProperties;

//    private ProjectVersionsProperties projectVersionsProperties;


    public HerbLiquibase liquibase(DataSourceProperties dbProps,
                                   DatabaseVersionProperties properties) {
        //开始数据库并执行该版本的changelogs
        HerbLiquibase liquibase = createSpringLiquibase(dbProps);

        String serviceName = properties.getServiceName();
        String logTable = "lg_db_change_log";
        String logLockTable = "lg_db_change_log_lock";
        if (StringUtils.hasText(serviceName)) {
            logTable = serviceName.trim().toLowerCase() + "_db_change_log";
            logLockTable = serviceName.trim().toLowerCase() + "_db_change_log_lock";
        }
        liquibase.setDatabaseChangeLogTable(logTable);
        liquibase.setDatabaseChangeLogLockTable(logLockTable);

        liquibase.setDropFirst(false);
        liquibase.setShouldRun(false);//重写，永远是不要自动执行

//        liquibase.setTestRollbackOnUpdate();
        //
        liquibase.setResourceLoader(resourceLoader);

        return liquibase;
    }

    private HerbLiquibase createSpringLiquibase(DataSourceProperties dataSourceProperties) {
        DataSource liquibaseDataSource = createNewDataSource(dataSourceProperties);
        HerbLiquibase liquibase = new HerbLiquibase();
        liquibase.setDataSource(liquibaseDataSource);
        return liquibase;
    }


    private DataSource createNewDataSource(DataSourceProperties dataSourceProperties) {
        String url = dataSourceProperties.getUrl();
        String user = dataSourceProperties.getUsername();
        String password = dataSourceProperties.getPassword();
        return DataSourceBuilder.create()
                .driverClassName(dataSourceProperties.getDriver())
                .url(url)
                .username(user)
                .password(password).build();
    }

    ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}

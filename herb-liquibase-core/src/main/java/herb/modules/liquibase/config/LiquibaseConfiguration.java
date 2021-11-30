package herb.modules.liquibase.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.function.Supplier;

//@Configuration(proxyBeanMethods = false)
//@EnableConfigurationProperties({ DataSourceProperties.class, LiquibaseProperties.class })
public class LiquibaseConfiguration {

    private final LiquibaseProperties properties;

    public LiquibaseConfiguration(LiquibaseProperties properties) {
        this.properties = properties;
    }


    public SpringLiquibase liquibase(DataSourceProperties dataSourceProperties,
                                     ObjectProvider<DataSource> dataSource,
                                     @LiquibaseDataSource ObjectProvider<DataSource> liquibaseDataSource) {
        SpringLiquibase liquibase = createSpringLiquibase(liquibaseDataSource.getIfAvailable(),
                dataSource.getIfUnique(), dataSourceProperties);
        liquibase.setChangeLog(this.properties.getChangeLog());
        liquibase.setContexts(this.properties.getContexts());
        liquibase.setDefaultSchema(this.properties.getDefaultSchema());
        liquibase.setLiquibaseSchema(this.properties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(this.properties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(this.properties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(this.properties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(this.properties.isDropFirst());


        liquibase.setShouldRun(false);//重写，永远是不要自动执行

        liquibase.setLabels(this.properties.getLabels());
        liquibase.setChangeLogParameters(this.properties.getParameters());
        liquibase.setRollbackFile(this.properties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(this.properties.isTestRollbackOnUpdate());
        return liquibase;
    }

    private SpringLiquibase createSpringLiquibase(DataSource liquibaseDatasource, DataSource dataSource,
                                                  DataSourceProperties dataSourceProperties) {
        DataSource liquibaseDataSource = getDataSource(liquibaseDatasource, dataSource);
        if (liquibaseDataSource != null) {
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(liquibaseDataSource);
            return liquibase;
        }
        SpringLiquibase liquibase = new DataSourceClosingSpringLiquibase();
        liquibase.setDataSource(createNewDataSource(dataSourceProperties));
        return liquibase;
    }

    private DataSource getDataSource(DataSource liquibaseDataSource, DataSource dataSource) {
        if (liquibaseDataSource != null) {
            return liquibaseDataSource;
        }
        if (this.properties.getUrl() == null && this.properties.getUser() == null) {
            return dataSource;
        }
        return null;
    }

    private DataSource createNewDataSource(DataSourceProperties dataSourceProperties) {
        String url = getProperty(this.properties::getUrl, dataSourceProperties::determineUrl);
        String user = getProperty(this.properties::getUser, dataSourceProperties::determineUsername);
        String password = getProperty(this.properties::getPassword, dataSourceProperties::determinePassword);
        return DataSourceBuilder.create().url(url).username(user).password(password).build();
    }

    private String getProperty(Supplier<String> property, Supplier<String> defaultValue) {
        String value = property.get();
        return (value != null) ? value : defaultValue.get();
    }
}

package herb.modules.liquibase;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({HerbLiquibaseProperties.class,ProjectVersionsProperties.class})
public class HerbLiquibaseAutoConfiguration {

    HerbLiquibaseProperties liquibaseProperties;

    ProjectVersionsProperties versionsProperties;

    public HerbLiquibaseAutoConfiguration(HerbLiquibaseProperties liquibaseProperties
    ,ProjectVersionsProperties versionsProperties){
        this.liquibaseProperties = liquibaseProperties;
        this.versionsProperties = versionsProperties;
    }

}

package herb.modules.liquibase;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HerbLiquibaseProperties.class)
public class HerbLiquibaseAutoConfiguration {

    HerbLiquibaseProperties liquibaseProperties;

    public HerbLiquibaseAutoConfiguration(HerbLiquibaseProperties liquibaseProperties){
        this.liquibaseProperties = liquibaseProperties;
    }



}

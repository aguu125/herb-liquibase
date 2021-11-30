package herb.modules.liquibase.config;

import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
//@EnableConfigurationProperties({HerbLiquibaseProperties.class})
public class HerbLiquibaseAutoConfiguration {

    HerbLiquibaseProperties liquibaseProperties;

//    ProjectVersionsProperties versionsProperties;

    public HerbLiquibaseAutoConfiguration(HerbLiquibaseProperties liquibaseProperties) {
        this.liquibaseProperties = liquibaseProperties;
//        this.versionsProperties = versionsProperties;
    }

}

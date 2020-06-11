package herb.modules.liquibase;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

//@Configuration
//@EnableConfigurationProperties({HerbLiquibaseProperties.class})
public class HerbLiquibaseAutoConfiguration {

    HerbLiquibaseProperties liquibaseProperties;

//    ProjectVersionsProperties versionsProperties;

    public HerbLiquibaseAutoConfiguration(HerbLiquibaseProperties liquibaseProperties) {
        this.liquibaseProperties = liquibaseProperties;
//        this.versionsProperties = versionsProperties;
    }

}

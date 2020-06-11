package herb.modules.liquibase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "liquibase")
public class HerbLiquibaseProperties {

    @Setter
    @Getter
    private Map<String, HerbDbLiquibaseProperties> databases = new HashMap<>();

}
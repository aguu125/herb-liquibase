package herb.modules.liquibase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ConfigurationProperties(prefix = "liquibase")
public class HerbLiquibaseProperties {
    @Setter
    @Getter
    private Map<String, HerbDbLiquibaseProperties> databases = new HashMap<>();

    @Getter
    @Setter
    private List<ProjectVersionDbChangelogProperties> versions = new ArrayList<>();
}
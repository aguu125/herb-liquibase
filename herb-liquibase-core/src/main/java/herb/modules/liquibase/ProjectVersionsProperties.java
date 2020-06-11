package herb.modules.liquibase;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "project")
@PropertySource(value ={"classpath:db/versions.yml"},factory = MixPropertySourceFactory.class)
public class ProjectVersionsProperties {

    @Getter
    @Setter
    private List<DatabaseVersionProperties> versions = new ArrayList<>();

    @Data
    public static class DatabaseVersionProperties {

        private String version;

        private List<DatabaseChangeLogProp> changeLogs = new ArrayList<>();
    }

    @Data
    public static class DatabaseChangeLogProp{
        private String database;
        private String changeLog;
    }

}
package herb.modules.liquibase.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "herb.liquibase")
public class HerbLiquibaseProperties {


    /**
     * 默认的数据源配置
     */
    @Getter
    @Setter
    private DataSourceProperties defaultDataSource;

    /**
     * 《数据库名称,数据库连接配置信息》
     * 多个数据库配置（预留）
     */
    @Setter
    @Getter
    private Map<String, DataSourceProperties> databases = new HashMap<>();

}
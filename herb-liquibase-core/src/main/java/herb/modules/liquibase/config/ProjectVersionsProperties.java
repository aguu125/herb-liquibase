package herb.modules.liquibase.config;

import herb.modules.liquibase.utils.MixPropertySourceFactory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 项目的版本及其对应的脚本配置
 */
@Data
@Component
@PropertySource(value = {"classpath:versions.yml"}, factory = MixPropertySourceFactory.class)
@ConfigurationProperties(prefix = "projects")
public class ProjectVersionsProperties {

    /**
     * 《服务名称,版本列表》
     */
    @Getter
    @Setter
    private List<DatabaseVersionProperties> services;

    @Data
    public static class DatabaseVersionProperties {
        /**
         * 服务名称
         */
        private String serviceName;
        /**
         * 数据库
         */
        private String database;

        /**
         * 对应版本的changelog
         */
        private List<VersionChangeLog> versions = new ArrayList<>();

    }

    @Data
    public static class VersionChangeLog {
        /**
         * 版本号
         */
        private String name;

        /**
         * 脚本
         */
        private String changeLog;
    }

}
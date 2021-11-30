package herb.modules;

import herb.modules.liquibase.HerbLiquibaseRunner;
import herb.modules.liquibase.HerbLiquibaseBuilder;
import herb.modules.liquibase.LiquibaseCommandRunner;
import herb.modules.liquibase.config.HerbLiquibaseProperties;
import herb.modules.liquibase.config.ProjectVersionsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

import java.util.Arrays;

/**
 * 通过 spring boot 启动参数，调用 liqubase 命令行参数
 * 排除掉spring boot 的自定义配置
 *
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        LiquibaseAutoConfiguration.class
})
public class StartApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    HerbLiquibaseProperties liquibaseProperties;

    ProjectVersionsProperties projectVersionsProperties;

    @Autowired
    public void setLiquibase(HerbLiquibaseProperties liquibaseProperties) {
        this.liquibaseProperties = liquibaseProperties;
    }

    @Autowired
    public void setProjectVersionsProperties(ProjectVersionsProperties projectVersionsProperties) {
        this.projectVersionsProperties = projectVersionsProperties;
    }

    @Autowired
    HerbLiquibaseBuilder  liquibaseBuilder;


    @Override
    public void run(String... args) throws Exception {
        log.info("\n\t CommandLine args:{} ------------", Arrays.asList(args));
//        log.info("bean: {}", liquibaseProperties);
        if (args != null && args.length > 0) {
            //final HerbLiquibaseRunner runner = new HerbLiquibaseRunner(liquibaseProperties, projectVersionsProperties);
//            runner.setLiquibaseBuilder(liquibaseBuilder);
            final LiquibaseCommandRunner runner = new LiquibaseCommandRunner(liquibaseProperties, projectVersionsProperties);
            runner.run(args);
        } else {
            log.warn("There are no CommandLine args, exits！！！");
        }

        log.info("------------- finished------------");
    }
}

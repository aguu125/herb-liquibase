package org.test.cbss;

import herb.modules.liquibase.HerbLiquibaseAutoConfiguration;
import herb.modules.liquibase.HerbLiquibaseProperties;
import herb.modules.liquibase.HerbLiquibaseRunner;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.integration.commandline.Main;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
//@ComponentScan(basePackages = "herb.modules.liquibase")
@ImportAutoConfiguration(HerbLiquibaseAutoConfiguration.class)
public class CbssSpringBootApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CbssSpringBootApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CbssSpringBootApplication.class, args);
    }

    HerbLiquibaseProperties liquibaseProperties;

    @Autowired
    public void setLiquibase(HerbLiquibaseProperties liquibaseProperties) {
        this.liquibaseProperties = liquibaseProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("------------ start to run ------------");
        log.info("args: {}", Arrays.asList(args));

//        liquibaseProperties
        log.info("bean: {}", liquibaseProperties);
        if(args != null && args.length > 0) {
            log.info("start main run...");
            HerbLiquibaseRunner runner = new HerbLiquibaseRunner(liquibaseProperties);
            runner.run(args);
            //Main.run(args);
            //liquibase.afterPropertiesSet();
        }
        log.info("------------- finished");
    }
}

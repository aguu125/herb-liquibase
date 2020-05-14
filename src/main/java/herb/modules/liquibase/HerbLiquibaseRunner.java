package herb.modules.liquibase;

import liquibase.configuration.GlobalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

//通过spring boot的 配置文件信息
//调用liquibase MAIN ，使用命令行模式来调用
public class HerbLiquibaseRunner {

    private static final Logger log = LoggerFactory.getLogger(HerbLiquibaseRunner.class);

    private HerbLiquibaseProperties liquibaseProperties;
    private ProjectVersionsProperties versionsProperties;

    public HerbLiquibaseRunner(HerbLiquibaseProperties springLiquibase,
                               ProjectVersionsProperties versionsProperties) {
        this.liquibaseProperties = springLiquibase;
        this.versionsProperties = versionsProperties;
    }

    //扩展支持updateToVersion

    public void run(String[] args) {
        int errorLevel = 0;

        ProjectVersionsProperties.DatabaseVersionProperties changelogProperties = null;

        if ("updateToVersion".equalsIgnoreCase(args[0])) {
            try {

                if (args.length < 2 || StringUtils.hasText(args[1]) == false) {
                    log.error("updateToVersion 命令缺少版本参数");
                    System.exit(-1);
                }

                String versionName = args[1];
                Optional<ProjectVersionsProperties.DatabaseVersionProperties> versionOpt =
                        versionsProperties.getVersions().stream()
                        .filter((prop) -> Objects.equals(prop.getVersion(), versionName))
                        .findFirst();

                if (versionOpt.isPresent()) {
                    changelogProperties = versionOpt.get();
                } else {
                    log.error("没有匹配的版本信息：{}", versionName);
                    System.exit(-1);
                }
                //重新构造args[]
                args = new String[]{"update"};
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                System.exit(-1);
            }
        } else {
            //取最新的版本
            if (!CollectionUtils.isEmpty(versionsProperties.getVersions())) {
                changelogProperties
                        = versionsProperties.getVersions().get(versionsProperties.getVersions().size() - 1);
            }
        }

        if (changelogProperties == null) {
            log.error("changelogProperties is null");
            System.exit(-1);
        }

        //开始执行版本
        List<ProjectVersionsProperties.DatabaseChangeLogProp> changeLogPropList
                = changelogProperties.getChangeLogs();
        for (int i = 0; i < changeLogPropList.size(); i++) {
            ProjectVersionsProperties.DatabaseChangeLogProp
                    databaseChangeLogProp = changeLogPropList.get(i);


            HerbDbLiquibaseProperties dbLiquibaseProperties = null;
            for (Map.Entry<String, HerbDbLiquibaseProperties> entry
                    : liquibaseProperties.getDatabases().entrySet()) {
                String databaseName = entry.getKey();
                if (databaseName.equals(databaseChangeLogProp.getDatabase())) {
                    dbLiquibaseProperties = entry.getValue();
                    break;
                }
            }

            if (dbLiquibaseProperties == null) {
                log.error("不存在{} 的数据库配置信息", databaseChangeLogProp.getDatabase());
                System.exit(-1);
            }

            runForDatabase(databaseChangeLogProp, dbLiquibaseProperties, args);
        }

        System.exit(errorLevel);
    }

    private int runForDatabase(ProjectVersionsProperties.DatabaseChangeLogProp databaseChangeLogProp,
                               HerbDbLiquibaseProperties properties, String[] args) {
        String dbName = databaseChangeLogProp.getDatabase();

        log.info("================== start to run database 【{}】,command is {}  ===========", dbName, args);

        String[] globalOptions = buildGlobalOptions(databaseChangeLogProp, properties);
        List<String> runArgs = new ArrayList<>();
        for (String globalOption : globalOptions) {
            runArgs.add(globalOption);
        }

        for (String arg : args) {
            runArgs.add(arg);
        }

        try {
            StringBuilder str = new StringBuilder();
            for(String arg: runArgs){
                str.append("\n  "+arg);
            }
            log.info("执行命令 \n liquibase {} ",str.toString());

            Main.run(runArgs.toArray(new String[runArgs.size()]));
        } catch (Throwable e) {
            System.exit(-1);
        }
        return 0;
    }

    private String[] buildGlobalOptions(ProjectVersionsProperties.DatabaseChangeLogProp databaseChangeLogProp,
                                        HerbDbLiquibaseProperties properties) {
        List<String> globalOptions = new ArrayList<>();

        String driver = properties.getDriver();
        String url = properties.getUrl();
        String userName = properties.getUsername();
        String password = properties.getPassword();


        globalOptions.add(String.format("--driver=%s", driver));
        globalOptions.add(String.format("--url=%s", url));
        globalOptions.add(String.format("--username=%s", userName));
        globalOptions.add(String.format("--password=%s", password));


        String changeLogFile
                = "/db/" + databaseChangeLogProp.getDatabase() + "/" + databaseChangeLogProp.getChangeLog();
        globalOptions.add(String.format("--changeLogFile=%s", changeLogFile));


        String logTable = "lg_db_change_log";
        if (StringUtils.hasText(properties.getDatabaseChangeLogTable())) {
            logTable = properties.getDatabaseChangeLogTable().trim().toLowerCase();
        }
        String logLockTable = "lg_db_change_log_lock";
        if (StringUtils.hasText(properties.getDatabaseChangeLogLockTable())) {
            logLockTable = properties.getDatabaseChangeLogLockTable().trim().toLowerCase();
        }
        globalOptions.add(String.format("--%s=%s", GlobalConfiguration.DATABASECHANGELOG_TABLE_NAME, logTable));
        globalOptions.add(String.format("--%s=%s", GlobalConfiguration.DATABASECHANGELOGLOCK_TABLE_NAME, logLockTable));


//        globalOptions.add(String.format("--defaultsFile=%s","C://liquibase.properties"));


        return globalOptions.toArray(new String[globalOptions.size()]);
    }

}

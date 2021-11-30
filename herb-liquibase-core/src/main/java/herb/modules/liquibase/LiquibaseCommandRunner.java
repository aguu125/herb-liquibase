package herb.modules.liquibase;

import herb.modules.liquibase.config.DataSourceProperties;
import herb.modules.liquibase.config.HerbLiquibaseProperties;
import herb.modules.liquibase.config.ProjectVersionsProperties;
import herb.modules.liquibase.config.ProjectVersionsProperties.DatabaseVersionProperties;
import herb.modules.liquibase.config.ProjectVersionsProperties.VersionChangeLog;
import liquibase.exception.LiquibaseException;
import liquibase.integration.commandline.LiquibaseCommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

//通过spring boot的 配置文件信息
//调用liquibase MAIN ，使用命令行模式来调用
public class LiquibaseCommandRunner {

    private static final Logger log = LoggerFactory.getLogger(LiquibaseCommandRunner.class);

    private HerbLiquibaseProperties liquibaseProperties;

    private ProjectVersionsProperties projectVersionsProperties;

    public LiquibaseCommandRunner(HerbLiquibaseProperties springLiquibase, ProjectVersionsProperties versionsProperties) {
        if (versionsProperties == null) {
            log.warn("can't find db/versions.yml");
        }
        this.projectVersionsProperties = versionsProperties;
        this.liquibaseProperties = springLiquibase;
    }

    //扩展支持updateToVersion

    public void run(String[] args) {
        int errorLevel = 0;

        List<String> databaseReport = new ArrayList<>();
        int successCount = 0;
        StringBuilder message = new StringBuilder("\n----------------\n");

        for (DatabaseVersionProperties serviceProperties : projectVersionsProperties.getServices()) {
            String serviceName = serviceProperties.getServiceName();
            if (CollectionUtils.isEmpty(serviceProperties.getVersions())) {
                log.error("没有任何版本配置信息");
                System.exit(-1);
            }
            VersionChangeLog versionChangeLog = null;

            if ("updateToVersion".equalsIgnoreCase(args[0])) {
                try {

                    if (args.length < 2 || StringUtils.hasText(args[1]) == false) {
                        log.error("updateToVersion 命令缺少版本参数.");
                        System.exit(-1);
                    }
                    //升级的版本号
                    String versionName = args[1];

                    Optional<VersionChangeLog> versionOpt =
                            serviceProperties.getVersions().stream()
                                    .filter((prop) -> Objects.equals(prop.getName(), versionName))
                                    .findFirst();

                    if (versionOpt.isPresent()) {
                        versionChangeLog = versionOpt.get();
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
                versionChangeLog = serviceProperties.getVersions().get(serviceProperties.getVersions().size() - 1);
            }


            if (versionChangeLog == null) {
                log.error("changelogProperties is null");
                System.exit(-1);
            } else {
                log.info("chosen version is  : [{}],changlog: {}",
                        versionChangeLog.getName(),
                        versionChangeLog.getChangeLog()
                );
            }

            //开始执行版本
            //开始数据库并执行该版本的changelogs
            DataSourceProperties dataSource = null;
            String dbName = serviceProperties.getDatabase();
            if (StringUtils.hasText(dbName)) {
                if (liquibaseProperties.getDatabases().containsKey(dbName)) {
                    dataSource = liquibaseProperties.getDatabases().get(dbName);
                }
            } else {
                dataSource = liquibaseProperties.getDefaultDataSource();
            }

            if (dataSource == null) {
//                log.error("没有数据源配置:{}", StringUtils.hasText(dbName) ? dbName : "defaultDataSource");
//                System.exit(-1);
                log.error("不存在{} 的数据库配置信息", dbName);
                databaseReport.add(dbName + "\t\t\t FAIL,读取不到数据库配置信息");
                //System.exit(-1);
                continue;
            }


            log.info("================== start to run database 【{}】,command is {}  ===========", dbName, args);

            try {
                int returnCode = runForDatabase(serviceName, versionChangeLog, dataSource, args);
                if (returnCode == 0) {
                    databaseReport.add(dbName + "\t\t\t OK.");
                    successCount++;
                } else {
                    databaseReport.add(dbName + "\t\t\t Fail,执行返回:" + returnCode);
                }

            } catch (Throwable e) {
                log.error(e.getMessage(), e);
                databaseReport.add(dbName + "\t\t\t FAIL,执行发布异常！");
            }
            message.append("\n" + serviceName + " 执行命令：");
            for (String arg : args) {
                message.append(arg + " ");
            }
            message.append("\n 执行结果：");
        }

        boolean isAllSuccess = successCount == 1;
        if (isAllSuccess) {
            message.append(" SUCCESS");
        } else {
            message.append(" FAIL");
        }

        message.append("\n 详细报告：");
        for (String dbReport : databaseReport) {
            message.append("\n " + dbReport);
        }
        message.append("\n----------------\n");
        log.info(message.toString());
        System.out.println(message.toString());
        if (isAllSuccess == false) {
            System.exit(-1);
        } else {
            System.exit(0);
        }
    }

    private int runForDatabase(String serviceName, VersionChangeLog databaseChangeLogProp,
                               DataSourceProperties properties, String[] args) throws LiquibaseException {

        String[] globalOptions = buildGlobalOptions(serviceName, databaseChangeLogProp, properties);

        int returnCode;
        //如果执行更新，强制打tag
        if ("update".equals(args[0])) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            String tagName = serviceName + "_startUpdate_" + sf.format(new Date());
            returnCode = runLiquibaseCommand(globalOptions, new String[]{"tag", tagName});
            if (returnCode == 0) {
                returnCode = runLiquibaseCommand(globalOptions, args);
            }
        } else {
            returnCode = runLiquibaseCommand(globalOptions, args);
        }
        return returnCode;
    }


    public int runLiquibaseCommand(String[] globalOptions, String[] args) {

        //参数准备
        final List<String> runArgs = new ArrayList<>();
        for (String globalOption : globalOptions) {
            runArgs.add(globalOption);
        }
        for (String arg : args) {
            runArgs.add(arg);
        }

        //logging
        StringBuilder str = new StringBuilder();
        for (String arg : runArgs) {
            str.append("\n  " + arg);
        }
        log.info("开始执行命令 \n liquibase {} ", str.toString());

        try {

            // 这里调用命令行无法处理日志问题
            final LiquibaseCommandLine cli = new LiquibaseCommandLine();
            int returnCode = cli.execute(runArgs.toArray(new String[0]));
            return returnCode;

//            Liquibase liquibase = new Liquibase();


        } catch (Throwable e) {
            throw e;
        }
    }


    /**
     * 生成liquibase global 的参数，对应liquibase.properties
     *
     * @param databaseChangeLogProp
     * @param properties
     * @return
     */
    private String[] buildGlobalOptions(String serviceName, VersionChangeLog databaseChangeLogProp,
                                        DataSourceProperties properties) {
        List<String> globalOptions = new ArrayList<>();

        String driver = properties.getDriver();
        String url = properties.getUrl();
        String userName = properties.getUsername();
        String password = properties.getPassword();
        String changeLogFile = databaseChangeLogProp.getChangeLog();

        globalOptions.add(String.format("--driver=%s", driver));
        globalOptions.add(String.format("--url=%s", url));
        globalOptions.add(String.format("--username=%s", userName));
        globalOptions.add(String.format("--password=%s", password));
        globalOptions.add(String.format("--changeLogFile=%s", changeLogFile));

        globalOptions.add(String.format("--logLevel=INFO"));

        //change_log file
        String logTable = "lg_db_change_log";
        String logLockTable = "lg_db_change_log_lock";
        if (StringUtils.hasText(serviceName)) {
            logTable = serviceName.trim().toLowerCase() + "_db_change_log";
            logLockTable = serviceName.trim().toLowerCase() + "_db_change_log_lock";
        }

        globalOptions.add(String.format("--%s=%s", "databaseChangeLogTableName", logTable));
        globalOptions.add(String.format("--%s=%s", "databaseChangeLogLockTableName", logLockTable));

//        globalOptions.add(String.format("--defaultsFile=%s","C://liquibase.properties"));

        return globalOptions.toArray(new String[globalOptions.size()]);
    }

}

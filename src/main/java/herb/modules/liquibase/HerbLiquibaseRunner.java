package herb.modules.liquibase;

import liquibase.configuration.GlobalConfiguration;
import liquibase.integration.commandline.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

//通过spring boot的 配置文件信息
//调用liquibase MAIN ，使用命令行模式来调用
public class HerbLiquibaseRunner {

    private static final Logger log = LoggerFactory.getLogger(HerbLiquibaseRunner.class);

    //global options
    private enum COMMANDS {
        ;
        private static final String CALCULATE_CHECKSUM = "calculateCheckSum";
        private static final String CHANGELOG_SYNC = "changelogSync";
        private static final String CHANGELOG_SYNC_SQL = "changelogSyncSQL";
        private static final String CLEAR_CHECKSUMS = "clearCheckSums";
        private static final String DB_DOC = "dbDoc";
        private static final String DIFF = "diff";
        private static final String DIFF_CHANGELOG = "diffChangeLog";
        private static final String DROP_ALL = "dropAll";
        private static final String EXECUTE_SQL = "executeSql";
        private static final String FUTURE_ROLLBACK_COUNT_SQL = "futureRollbackCountSQL";
        private static final String FUTURE_ROLLBACK_FROM_TAG_SQL = "futureRollbackFromTagSQL";
        private static final String FUTURE_ROLLBACK_SQL = "futureRollbackSQL";
        private static final String FUTURE_ROLLBACK_TO_TAG_SQL = "futureRollbackToTagSQL";
        private static final String GENERATE_CHANGELOG = "generateChangeLog";
        private static final String HELP = OPTIONS.HELP;
        private static final String HISTORY = "history";
        private static final String LIST_LOCKS = "listLocks";
        private static final String MARK_NEXT_CHANGESET_RAN = "markNextChangeSetRan";
        private static final String MARK_NEXT_CHANGESET_RAN_SQL = "markNextChangeSetRanSQL";
        private static final String MIGRATE = "migrate";
        private static final String MIGRATE_SQL = "migrateSQL";
        private static final String RELEASE_LOCKS = "releaseLocks";
        private static final String ROLLBACK_ONE_CHANGE_SET = "rollbackOneChangeSet";
        private static final String ROLLBACK_ONE_CHANGE_SET_SQL = "rollbackOneChangeSetSQL";
        private static final String ROLLBACK_ONE_UPDATE = "rollbackOneUpdate";
        private static final String ROLLBACK_ONE_UPDATE_SQL = "rollbackOneUpdateSQL";
        private static final String ROLLBACK = "rollback";
        private static final String ROLLBACK_COUNT = "rollbackCount";
        private static final String ROLLBACK_COUNT_SQL = "rollbackCountSQL";
        private static final String ROLLBACK_SCRIPT = "rollbackScript";
        private static final String ROLLBACK_SQL = "rollbackSQL";
        private static final String ROLLBACK_TO_DATE = "rollbackToDate";
        private static final String ROLLBACK_TO_DATE_SQL = "rollbackToDateSQL";
        private static final String SNAPSHOT = "snapshot";
        private static final String SNAPSHOT_REFERENCE = "snapshotReference";
        private static final String STATUS = "status";
        private static final String TAG = "tag";
        private static final String TAG_EXISTS = "tagExists";
        private static final String UNEXPECTED_CHANGESETS = "unexpectedChangeSets";
        private static final String UPDATE = "update";
        private static final String UPDATE_COUNT = "updateCount";
        private static final String UPDATE_COUNT_SQL = "updateCountSQL";
        private static final String UPDATE_SQL = "updateSQL";
        private static final String UPDATE_TESTING_ROLLBACK = "updateTestingRollback";
        private static final String UPDATE_TO_TAG = "updateToTag";
        private static final String UPDATE_TO_TAG_SQL = "updateToTagSQL";
        private static final String VALIDATE = "validate";
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private enum OPTIONS {
        ;
        private static final String VERBOSE = "verbose";
        private static final String CHANGELOG_FILE = "changeLogFile";
        private static final String DATA_OUTPUT_DIRECTORY = "dataOutputDirectory";
        private static final String DIFF_TYPES = "diffTypes";
        private static final String CHANGE_SET_ID = "changeSetId";
        private static final String CHANGE_SET_AUTHOR = "changeSetAuthor";
        private static final String CHANGE_SET_PATH = "changeSetPath";
        private static final String DEPLOYMENT_ID = "deploymentId";
        private static final String OUTPUT_FILE = "outputFile";
        private static final String FORCE = "force";
        private static final String ROLLBACK_SCRIPT = "rollbackScript";
        private static final String EXCLUDE_OBJECTS = "excludeObjects";
        private static final String INCLUDE_CATALOG = "includeCatalog";
        private static final String INCLUDE_OBJECTS = "includeObjects";
        private static final String INCLUDE_SCHEMA = "includeSchema";
        private static final String INCLUDE_TABLESPACE = "includeTablespace";
        private static final String OUTPUT_SCHEMAS_AS = "outputSchemasAs";
        private static final String REFERENCE_DEFAULT_CATALOG_NAME = "referenceDefaultCatalogName";
        private static final String REFERENCE_DEFAULT_SCHEMA_NAME = "referenceDefaultSchemaName";
        private static final String REFERENCE_DRIVER = "referenceDriver";
        // SONAR confuses this constant name with a hard-coded password:
        @SuppressWarnings("squid:S2068")
        private static final String REFERENCE_PASSWORD = "referencePassword";
        private static final String REFERENCE_SCHEMAS = "referenceSchemas";
        private static final String REFERENCE_URL = "referenceUrl";
        private static final String REFERENCE_USERNAME = "referenceUsername";
        private static final String SCHEMAS = "schemas";
        private static final String URL = "url";
        private static final String HELP = "help";
        private static final String VERSION = "version";
        private static final String SNAPSHOT_FORMAT = "snapshotFormat";
        private static final String LOG_FILE = "logFile";
        private static final String LOG_LEVEL = "logLevel";
    }

    private HerbLiquibaseProperties liquibaseProperties;

    public HerbLiquibaseRunner(HerbLiquibaseProperties springLiquibase) {
        this.liquibaseProperties = springLiquibase;
    }

    //扩展支持updateToVersion

    public void run(String[] args) {
        int errorLevel = 0;

        ProjectVersionDbChangelogProperties changelogProperties = null;

        if ("updateToVersion".equalsIgnoreCase(args[0])) {
            try {

                if(args.length < 2 || StringUtils.hasText(args[1])==false){
                    log.error("updateToVersion 命令缺少版本参数");
                    System.exit(-1);
                }

                String versionName = args[1];
                Optional<ProjectVersionDbChangelogProperties> versionOpt = liquibaseProperties.getVersions().stream()
                        .filter((prop) -> Objects.equals(prop.getVersion(), versionName))
                        .findFirst();

                if (versionOpt.isPresent()) {
                    changelogProperties = versionOpt.get();
                }else{
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
            if(!CollectionUtils.isEmpty(liquibaseProperties.getVersions())){
                 changelogProperties
                        = liquibaseProperties.getVersions().get(liquibaseProperties.getVersions().size()-1);

            }

        }
        if(changelogProperties==null){
            log.error("changelogProperties is null");
            System.exit(-1);
        }

        //开始执行版本
        List<ProjectVersionDbChangelogProperties.DatabaseChangeLogProp> changeLogPropList
                = changelogProperties.getChangeLogs();
        for (int i = 0; i < changeLogPropList.size(); i++) {
            ProjectVersionDbChangelogProperties.DatabaseChangeLogProp databaseChangeLogProp
                    = changeLogPropList.get(i);



            HerbDbLiquibaseProperties dbLiquibaseProperties=null;
            for (Map.Entry<String, HerbDbLiquibaseProperties> entry
                    : liquibaseProperties.getDatabases().entrySet()) {
                String databaseName = entry.getKey();
                if(databaseName.equals(databaseChangeLogProp.getDatabase())){
                    dbLiquibaseProperties = entry.getValue();
                    break;
                }
            }

            if(dbLiquibaseProperties==null){
                log.error("不存在{} 的数据库配置信息",databaseChangeLogProp.getDatabase());
                System.exit(-1);
            }

            runForDatabase(databaseChangeLogProp, dbLiquibaseProperties, args);

        }



        System.exit(errorLevel);
        /*
        if(Objects.equals("agreement",databaseName)){

        }else if(Objects.equals("user",databaseName)){

        }else{
            throw new IllegalArgumentException("args 参数数据库无法识别:"+args[0]);
        }
        */


    }

    private int runForDatabase(ProjectVersionDbChangelogProperties.DatabaseChangeLogProp databaseChangeLogProp,
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
            Main.run(runArgs.toArray(new String[runArgs.size()]));
        } catch (Throwable e) {
            System.exit(-1);
        }
        return 0;
    }

    private String[] buildGlobalOptions(ProjectVersionDbChangelogProperties.DatabaseChangeLogProp databaseChangeLogProp,
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
                = "/db/" + databaseChangeLogProp.getDatabase() + "/"+databaseChangeLogProp.getChangeLog();
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


        return globalOptions.toArray(new String[globalOptions.size()]);
    }

}

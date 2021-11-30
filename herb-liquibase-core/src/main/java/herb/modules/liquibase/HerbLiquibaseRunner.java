package herb.modules.liquibase;

import herb.modules.liquibase.config.DataSourceProperties;
import herb.modules.liquibase.config.HerbLiquibaseProperties;
import herb.modules.liquibase.config.ProjectVersionsProperties;
import herb.modules.liquibase.config.ProjectVersionsProperties.DatabaseVersionProperties;
import herb.modules.liquibase.config.ProjectVersionsProperties.VersionChangeLog;
import herb.modules.liquibase.core.HerbLiquibase;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

//通过spring boot的 配置文件信息
//调用liqubase 对象执行
public class HerbLiquibaseRunner {

    private static final Logger log = LoggerFactory.getLogger(HerbLiquibaseRunner.class);

    private HerbLiquibaseProperties liquibaseProperties;

    private ProjectVersionsProperties projectVersionsProperties;

    @Setter
    HerbLiquibaseBuilder liquibaseBuilder;


    public HerbLiquibaseRunner(HerbLiquibaseProperties springLiquibase, ProjectVersionsProperties versionsProperties) {
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
                run(args, serviceProperties, dataSource);

                databaseReport.add(dbName + "\t\t\t OK.");
                successCount++;
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
        if (isAllSuccess == false) {
            System.exit(-1);
        } else {
            System.exit(0);
        }
    }

    private void run(String[] args,
                     DatabaseVersionProperties serviceProperties,
                     DataSourceProperties properties) throws LiquibaseException, SQLException {
        String serviceName = serviceProperties.getServiceName();

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

        HerbLiquibase herbLiquibase = liquibaseBuilder.liquibase(properties, serviceProperties);
        //如果执行更新，强制打tag
        if ("tag".equals(args[0])) {
            Liquibase liquibase = herbLiquibase.createLiquibase();
            liquibase.tag(args[1]);
        } else if ("update".equals(args[0])) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            String tagName = serviceName + "_lastUpdateAt_" + sf.format(new Date());
            Liquibase liquibase = herbLiquibase.createLiquibase();
            StringWriter writer = new StringWriter();

            liquibase.update("",writer);

            log.info(writer.getBuffer().toString());

            //liquibase.tag(tagName);
            //herbLiquibase.setChangeLog(versionChangeLog.getChangeLog());
//            herbLiquibase.tagAndUpdate(tagName);
//        }

        }
    }
}

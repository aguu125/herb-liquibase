# herb-liquibase
基于liquibase ，和springboot命令行模式 进行数据库脚本管理和升级

## dist目录介绍
dist/
+ herb-liquibase-1.0.jar //核心执行jar
+ application-local.yml //数据库连接配置文件，可以按照环境多个application-*.yml
+ update.sh //执行sh脚本 
+ resources/db  //数据库升级脚本目录
    ++ versions.yml //版本管理文件
    ++ cbss-db //数据库名称（非实际数据库名,与versions.yml,applicaton-local.yml 里面指定名称一致就可以）    
        ++ initializ //初始版本脚本目录
           ++ v1.0 //表示初始脚本从1.0开始
                ++ script_suite.xml 版本sql维护
                ++ 001_xx.sql,002_xx.sql .... // 数据库脚本
        ++ update //升级脚本目录 
            ++ v1.1 //
                 ++ script_suite.xml 版本sql维护
                 ++ 001_xx.sql,002_xx.sql .... // 数据库脚本
            ++ v1.2 //
            ...
            
##使用说明：
主要分为两个部分：
## （一）运维升级
配置数据库连接属性，application-*.yml
可以多个环境,如: 
* application-local.yml (默认)
* application-dev.yml (开发环境)
* application-test.yml （测试环境）
* application-prod.yml （生产环境）

然后通过执行脚本升级，update.sh : 
---
<code>
./update.sh 【local/test/dev/prod】
</code>

升级后会打印脚本执行报告，都是ok就可以了

## (二 ) 升级脚本维护
2.1 versions.yml添加新版本的change-log
```
project.versions:
    ## 1.0 版本
  - version: v1.0
    change-logs:
    - {database: agreement-db,change-log: v1.0_changelog.xml}
    
    ## 1.1 版本
  - version: v1.1
    change-logs:
    - {database: agreement-db,change-Log: v1.1_changelog.xml}
```
维护版本changelog
v1.0_changelog.xml
``` 
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">

    <include file="initialize/script_suite.xml" relativeToChangelogFile="true"></include>

</databaseChangeLog>
```
v1.1_changelog.xml
```
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">

    <include file="initialize/script_suite.xml" relativeToChangelogFile="true"></include>

    <include file="update/v1.1/script_suite.xml" relativeToChangelogFile="true"></include>

</databaseChangeLog>
```


2.2 脚本目录放在db/{数据库命名}/update/{版本号}
命名规范，例如：001_add_customer_table.sql
注意：一个 <changeSet> 的执行是在一个事务里面，所以引入的一个sql的脚本都要保证是在一个事务内，
避免执行失败后再次执行会报错。

2.3 配置版本
将sql 配置到 对应版本的script_suite.xml

```
<changeSet id="v1.1_002_20200509" author="wanghm">
        <comment>和</comment>
        <sqlFile path="002_add_hr.sql" relativeToChangelogFile="true"></sqlFile>
        <rollback>
            <sqlFile path="001_add_hr_rollback.sql"  relativeToChangelogFile="true"></sqlFile>
        </rollback>
        
    </changeSet>
```
1. 一定要提供对应 rollback脚本（dll 类型修改一定，dml数据类修改可选），以便后期提供版本回滚
2. id命名规范：${版本号}+${3位编号}+${提交时间},一定写记录author
3. 已提交执行的过的 sql 和changeSet 不能修改，不要去修改！ （已执行过的 changeSet 文件code 校验 失败）









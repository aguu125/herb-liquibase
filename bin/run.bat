echo off
echo /******************************************/
echo /******database update tools (v1.2)*******/
echo /*****************************************/
echo  update
echo  tag {tag version}
echo  rollback {tag version}
echo  updateToVersion {tag version}
echo  please input your command...

set /p ARGS= 

java -Xbootclasspath/a:./resources -jar herb-liquibase-1.0.jar %ARGS%

pause
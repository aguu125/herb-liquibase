#!/bin/bash


echo database update command
echo -------------------------------
echo  update
echo	tag {tag version}
echo	rollback {tag version}
echo	updateToVersion {tag version}
echo	exit
echo  -------------------------------
echo
echo  please input your command...

ECHO

read ARGS

java -Xbootclasspath/a:./resources -jar -Dfile.encoding=utf-8 herb-liquibase-1.0.jar $ARGS

##echo input is "$ARGS"

ECHO ...
#!/bin/bash

ver=v1.0
echo 执行初始化,标记初始化版本为${ver}
ARGS="tag $ver"
java -Xbootclasspath/a:./resources -jar -Dfile.encoding=utf-8 herb-liquibase-1.0.jar $ARGS

ECHO ...
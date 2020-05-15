#!/bin/bash

PROFILE="local"

if [ "$1" == "" ]; then
  PROFILE="local"
else
  PROFILE="$1"
fi

echo "数据库配置使用 application-$PROFILE.yml"


ver=v1.0
echo 执行初始化,标记初始化版本为${ver}
ARGS="tag $ver"
java -Xbootclasspath/a:./resources -jar -Dfile.encoding=utf-8 -Dspring.profiles.active=$PROFILE herb-liquibase-1.0.jar $ARGS

ECHO ...
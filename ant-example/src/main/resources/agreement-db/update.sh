PROFILE=""

if [ "$1" == "" ]; then
  PROFILE="local"
else
  PROFILE="$1"
fi

echo "数据库脚本使用 application-$PROFILE.yml"

ARGS=update

java -Xbootclasspath/a:./data -Xbootclasspath/a:./lib/mysql-connector-java-8.0.19.jar  -Dfile.encoding=utf-8 -Dspring.profiles.active=$PROFILE -jar lib/herb-liquibase-1.0.jar $ARGS

echo finish!
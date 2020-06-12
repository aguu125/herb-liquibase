echo "run: $@"
echo "database: application-local.yml"

java -Xbootclasspath/a:./data -Xbootclasspath/a:./lib/mysql-connector-java-8.0.19.jar  -Dfile.encoding=utf-8 -jar lib/herb-liquibase-1.0.jar $@

echo finish!
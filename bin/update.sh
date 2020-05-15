#!/bin/bash


ARGS=update

java -Xbootclasspath/a:./resources -jar -Dfile.encoding=utf-8 herb-liquibase-1.0.jar $ARGS


ECHO ...
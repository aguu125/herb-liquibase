@echo off
set PROFILE=%1

if "%PROFILE%" == "" (
    echo profile:pack
    mvn clean package -Ppack -DskipTests
    echo profile:pack
    pause
) else (
    echo profile:%PROFILE%
    mvn clean package -P%PROFILE% -DskipTests
    echo profile:%PROFILE%
    pause
)
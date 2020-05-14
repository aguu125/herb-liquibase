cls
@echo off
TITLE database update tools (v1.0)
:MENU
ECHO.
echo. ******************************************
echo. ******database update tools (v1.2)*******
echo. *****************************************
echo.  -------------------------------
echo.	update
echo.	tag {tag version}
echo.	rollback {tag version}
echo.	updateToVersion {tag version}
echo.	exit
echo.  -------------------------------

ECHO.
echo.  please input your command...

ECHO.

set /p ARGS=

IF "%ARGS%"=="exit" EXIT

PAUSE
ECHO.

java -Xbootclasspath/a:./resources -jar herb-liquibase-1.0.jar %ARGS%

ECHO.  -----------------------------

goto MENU
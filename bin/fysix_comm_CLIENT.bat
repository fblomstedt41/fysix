@echo off
TITLE %0

set FYSIX_DIR=lib
set JAVA=C:\j2sdk1.4.2_15\bin\java.exe

set FYSIX_JARS=%FYSIX_DIR%\fysix_comm.jar

cd ..
rem %JAVA% -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -classpath
%JAVA% -classpath "%FYSIX_JARS%" se.forcer.CommTester CLIENT

pause

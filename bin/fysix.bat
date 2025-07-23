@echo off
TITLE %0

set FYSIX_DIR=lib
rem set JAVA=C:\j2sdk1.4.2_15\bin\java.exe
rem set JAVA=C:\Program\java\j2sdk1.4.2_10\bin\java.exe
rem set JAVA=C:\Program\java\jre1.6.0_03\bin\java.exe
rem set JAVA=C:\Program\Java\jdk1.6.0_14\bin\java.exe
set JAVA="C:\Program Files\Java\jre1.8.0_241\bin\java.exe"

set FYSIX_JARS=%FYSIX_DIR%\fysix.jar
set FYSIX_JARS=%FYSIX_JARS%;%FYSIX_DIR%\javax_vecmath.jar
set FYSIX_JARS=%FYSIX_JARS%;%FYSIX_DIR%\janalogtv.jar

cd ..
rem %JAVA% -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -classpath
%JAVA% -classpath "%FYSIX_JARS%" fysix.FysixMain

pause

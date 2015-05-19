@ECHO off

cd ".\client"
javac ./*.java ./Heuristic/*.java ./Search/*.java

cd ..

if exist level.save (
    set /p LASTLEVEL=<level.save
) else (
    set LASTLEVEL=MAmultiagentSort.lvl
)

SET LEVEL=%1
if "x%LEVEL%"=="x" (
  set /p LEVEL=Enter level or press enter for %LASTLEVEL%:
)

if "x%LEVEL%"=="x" (
  set LEVEL=%LASTLEVEL%
)

if "%LEVEL:~-4%" neq ".lvl" (
  set LEVEL=%LEVEL%.lvl
)

SET DIRECTORY=levels
if not exist %DIRECTORY%/%LEVEL% (
    set DIRECTORY=complevels
)

if not exist %DIRECTORY%/%LEVEL% (
    @echo Level %LEVEL% not found!
    exit
)

@echo %LEVEL%>level.save

java -jar server.jar -l %DIRECTORY%/%LEVEL% -c "java client.Client" -g

taskkill /im java.exe /f

echo "*** Process finished ***
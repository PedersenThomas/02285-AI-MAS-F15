@ECHO off

cd ".\client"
javac ./*.java

cd ..

SET LEVEL=SAsokobanLevel96.lvl
IF NOT "%1"=="" SET LEVEL=%1

java -jar server.jar -l levels/%LEVEL% -c "java client.Client" -g

taskkill /im java.exe /f

echo "*** Process finished ***
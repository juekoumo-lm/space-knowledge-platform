@echo off
chcp 65001
echo 正在下载Tomcat 9...
if not exist "apache-tomcat-9.0.89.zip" (
    powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.89/bin/apache-tomcat-9.0.89.zip' -OutFile 'apache-tomcat-9.0.89.zip'"
)
echo 正在解压Tomcat...
if not exist "apache-tomcat-9.0.89" (
    powershell -Command "Expand-Archive -Path 'apache-tomcat-9.0.89.zip' -DestinationPath '.'"
)
echo 正在复制WAR文件到Tomcat...
copy /y "target\space-knowledge.war" "apache-tomcat-9.0.89\webapps\"
echo 正在启动Tomcat...
cd "apache-tomcat-9.0.89\bin"
startup.bat
pause

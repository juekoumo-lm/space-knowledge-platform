@echo off
chcp 65001
echo 正在清理并构建项目...
mvn clean package -Dmaven.test.skip=true
echo 构建完成，正在启动服务器...
java -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar target/space-knowledge.war
pause

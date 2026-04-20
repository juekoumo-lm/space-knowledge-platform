@echo off
chcp 65001
echo 正在启动航天知识闯关学习平台...
set MAVEN_OPTS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8
mvn jetty:run
pause

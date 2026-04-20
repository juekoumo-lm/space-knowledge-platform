@echo off
chcp 65001
echo 正在设置编码参数...
set MAVEN_OPTS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dorg.eclipse.jetty.LEVEL=DEBUG
echo 正在启动服务器并显示详细日志...
mvn jetty:run -Djetty.port=8081 -X
pause

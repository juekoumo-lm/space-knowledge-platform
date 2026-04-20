@echo off
chcp 65001 >nul
echo Starting Jetty server...
mvn jetty:run -Djetty.port=8081
pause
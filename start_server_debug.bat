@echo off
chcp 65001
echo ========================================
echo 启动航天知识闯关学习平台服务器
echo ========================================
echo.
echo 正在启动服务器，请等待...
echo 服务器启动后，请在浏览器中访问：
echo http://localhost:8081/space-knowledge/
echo.
echo 按 Ctrl+C 停止服务器
echo ========================================
echo.
mvn jetty:run
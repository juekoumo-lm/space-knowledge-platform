@echo off
chcp 65001 >nul
echo ======================================
echo 航天知识闯关学习平台启动脚本
echo ======================================
echo 清理依赖...
echo 正在清理本地 Maven 仓库...
call mvn dependency:purge-local-repository -q
echo 依赖清理完成！
echo.
echo 重新构建项目...
echo 正在编译和打包项目...
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo 构建失败！请检查错误信息。
    pause
    exit /b %errorlevel%
)
echo 项目构建成功！
echo.
echo 启动服务器...
echo 正在启动 Jetty 服务器...
echo 访问地址: http://localhost:8081/space-knowledge/
echo 按 Ctrl+C 停止服务器
echo ======================================
call mvn jetty:run -Djetty.port=8081
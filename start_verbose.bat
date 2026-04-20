@echo off
chcp 65001
echo 当前目录: %cd%
echo 正在设置编码参数...
set MAVEN_OPTS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8
echo 正在构建项目...
mvn clean package -Dmaven.test.skip=true
if %errorlevel% neq 0 (
    echo 构建失败！
    pause
    exit /b 1
)
echo 构建成功，正在启动服务器...
echo 尝试使用Jetty插件启动...
mvn jetty:run
pause

@echo off
chcp 65001
echo 正在启动服务器...
echo 使用Java命令直接运行WAR文件...
echo 端口: 8081
echo 上下文路径: /space-knowledge

echo 按任意键开始启动...
pause > nul

java -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dspring.profiles.active=dev -jar target/space-knowledge.war

pause

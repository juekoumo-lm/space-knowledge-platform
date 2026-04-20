Write-Host "正在设置编码参数..."
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

Write-Host "正在构建项目..."
& mvn clean package -Dmaven.test.skip=true

if ($LASTEXITCODE -ne 0) {
    Write-Host "构建失败！" -ForegroundColor Red
    pause
    exit 1
}

Write-Host "构建成功，正在启动服务器..."
Write-Host "尝试使用Java命令直接运行WAR文件..."

# 使用Java命令直接运行WAR文件
& java -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar target/space-knowledge.war

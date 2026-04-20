Write-Host "正在设置编码参数..."
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

Write-Host "正在构建项目..."
& mvn clean package -Dmaven.test.skip=true > build.log 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "构建失败！查看build.log获取详细信息..." -ForegroundColor Red
    Get-Content build.log
    pause
    exit 1
}

Write-Host "构建成功，正在启动服务器..."
Write-Host "尝试使用Jetty插件启动..."

# 使用Jetty插件启动，并重定向输出到文件
& mvn jetty:run > server.log 2>&1

Write-Host "服务器启动完成，查看server.log获取详细信息..."
Get-Content server.log

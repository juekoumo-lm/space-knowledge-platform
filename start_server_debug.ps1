# 启动服务器并显示调试信息
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "启动航天知识闯关学习平台服务器" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "正在启动服务器，请等待..." -ForegroundColor Yellow
Write-Host "服务器启动后，请在浏览器中访问：" -ForegroundColor Yellow
Write-Host "http://localhost:8081/space-knowledge/" -ForegroundColor Green
Write-Host ""
Write-Host "按 Ctrl+C 停止服务器" -ForegroundColor Red
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 启动服务器
mvn jetty:run
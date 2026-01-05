$env:SERVER_PORT="8080"
$env:DB_PASS=""
$env:UPLOAD_PATH="./uploads"

Write-Host "----------------------------------------------" -ForegroundColor Green
Write-Host "Lancement du Backend MOSCEPA (Mode Production)" -ForegroundColor Green
Write-Host "Port : $env:SERVER_PORT"
Write-Host "----------------------------------------------"

java -jar target/moscepa-backend-1.0.0.jar `
    --server.port=$env:SERVER_PORT `
    --spring.datasource.password=$env:DB_PASS `
    --file.upload-dir=$env:UPLOAD_PATH

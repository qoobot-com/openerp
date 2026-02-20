@echo off
echo ========================================
echo QooERP Frontend - 启动脚本
echo ========================================
echo.

echo [1/3] 检查依赖...
if not exist "node_modules\" (
    echo 依赖未安装,正在安装...
    cd ..
    pnpm install
    cd packages\web
) else (
    echo 依赖已安装
)

echo.
echo [2/3] 清理缓存...
if exist "dist\" (
    rmdir /s /q dist
    echo 已清理 dist 目录
)

echo.
echo [3/3] 启动开发服务器...
echo.
echo 服务器地址: http://localhost:3000
echo 按 Ctrl+C 停止服务器
echo ========================================
echo.

pnpm run dev

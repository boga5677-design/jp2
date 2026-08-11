@echo off
where gradle >nul 2>nul || (echo Gradle is unavailable. Install Gradle 8.11.1. & exit /b 1)
gradle %*

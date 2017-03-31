@echo off
echo begin running
if exist .\outputs (
	del /q .\outputs\*
)
for /f "delims=" %%i in ('dir .\outputs\temp\*.apk /b') do (
	zipalign -v 4  outputs\temp\%%i .\outputs\%%i
)
rd /s /q .\outputs\temp
echo end running
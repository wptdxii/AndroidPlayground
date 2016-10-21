@echo off
if exist .\buildsystem (
	cd .\buildsystem
)
start python channel.py
exit
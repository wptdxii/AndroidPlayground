# 使用方法
## 1.将需要生成多渠道的apk包放入根目录，可放入多个包
## 2.在channels.txt文件中添加渠道名，用换行隔开
## 3.配置python环境，并将sdk中的zipalign路径配置到环境变量
## 4.在当前目录下的命令行中，输入 'python channel.py'即可,配置channel.bat批处理后，可直接 执行命令：channel
## 5.360的渠道包需要加固签名后，再重新写入渠道号
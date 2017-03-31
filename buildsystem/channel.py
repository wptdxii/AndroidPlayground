# coding=utf-8
import zipfile
import shutil
import os
import sys

if __name__ == '__main__':
    apkFiles = []
    for file in os.listdir('.'):
        if os.path.isfile(file):
            extension = os.path.splitext(file)[1][1:]
            if extension in 'apk':
                apkFiles.append(file)

    for apkFile in apkFiles: 
        apkFullName = os.path.basename(apkFile)
        apkName = os.path.splitext(apkFullName)[0]

        output_dir = 'outputs/temp'
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)

        channel_file = './channels.txt'
        f = open(channel_file)
        lines = f.readlines()
        f.close()

        emptyFile = 'xxx.txt'
        f = open(emptyFile, 'w')
        f.close()

        for line in lines:
            channel = line.strip()
            destApkFile = './outputs/temp/%s_%s.apk' % (apkName, channel)
            shutil.copy(apkFile,  destApkFile)
            zipped = zipfile.ZipFile(destApkFile, 'a', zipfile.ZIP_DEFLATED)
            channelFile = "META-INF/channel_{channel}".format(channel = channel)
            zipped.write(emptyFile, channelFile)
            zipped.close()
        os.remove('./xxx.txt')

    #mac
    #os.system('chmod u+x zipalign_batch.sh')
    #os.system('./zipalign_batch.sh')

    #windows
    os.system('zipalign_batch.bat')


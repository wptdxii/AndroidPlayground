package com.wptdxii.androidpractice.widget.recycleradapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import org.kymjs.kjframe.utils.CipherUtils;
import org.kymjs.kjframe.utils.SystemTool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * FileUtils
 *
 * @author 赵辉
 * @2015-11-2 上午11:21:12
 */

public class FileUtils {

    public static final String HOME_PATH = getSDCardPath() + "/ubaby/";
    public static final String DOWNLOAD_PATH = HOME_PATH + "download/";
    public static final String CACHE_PATH = HOME_PATH + "cache/";
    public static final String LRC_PATH = HOME_PATH + "lrc/";
    public static final String CAMEAR_PATH = getSDCardPath() + "/DCIM/Camera/";
    public static final String RECORD_AUDIO_PATH = HOME_PATH + "AudioRecorder/";


    public static final String ENCRYPT_FILE_EXT = ".ubaby";
    public static final String DECRYPT_FILE_EXT = ".ubaby2";

    private static final String AES = "AES";
    private static final String CRYPT_KEY = "UBABYtestTestBay";

    // 创建文件夹
    public static void createSDDir() {
        File file = new File(HOME_PATH);
        if (!file.exists()) {
            boolean result = file.mkdir();
            if (!result) {
                Trace.e("创建根目录失败");
                return;
            }
        }

        file = new File(DOWNLOAD_PATH);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Trace.e("创建下载目录失败");
                return;
            }
        }

        file = new File(CACHE_PATH);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Trace.e("创建缓存目录失败");
                return;
            }
        }

        file = new File(CAMEAR_PATH);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Trace.e("创建相册目录失败");
                return;
            }
        }

        file = new File(RECORD_AUDIO_PATH);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Trace.e("创建录音目录失败");
                return;
            }
        }

        file = new File(LRC_PATH);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Trace.e("创建歌词目录失败");
                return;
            }
        }
    }

    public static File getDownloadFile(String fileName) {
        File file = new File(DOWNLOAD_PATH + CipherUtils.md5(fileName));
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File getDownloadLrc(String fileName) {
        File file = new File(LRC_PATH + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getAPKFilePath() {
        return HOME_PATH + "ubaby.apk";
    }

    public static void clearCache() {
        deleteMd5File(new File(CACHE_PATH));
    }

    public static String getFilePathInFilesDir(Context context, String fileName) {
        return context.getFilesDir().getAbsolutePath() + File.separator + fileName;
    }

    public static boolean checkSDcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    // 使用ACTION_PICK从系统相册获取的照片
    public static String getPickImagePath(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    public static String getPickImagePath(Context context, Intent intent) {
        Uri uri = geturi(context, intent);
        return getPickImagePath(context, uri);
        //  8/11/16 付红杰把小米相册改好了  但是上传又完蛋了
        //  事实证明 付红杰改的没错 上传不好使是网络完蛋了  欧耶
        //return getPickImagePath(context,intent.getData());
    }

    private static Uri geturi(Context activity, android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = activity.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;

                    }
                }
            }
        }
        return uri;
    }

    //递归删除文件夹下所有文件
    public static void deleteMd5File(File file) {
        if (file == null || !file.exists())
            return;

        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteMd5File(f);
            }
            file.delete();
        }
    }

    /**
     * 删除音频文件
     *
     * @param filePath
     */
    public static void deleteAudioFile(String filePath) {
        File file = new File(FileUtils.DOWNLOAD_PATH + CipherUtils.md5(filePath) + ENCRYPT_FILE_EXT);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        deleteDecryptFile(file.getPath());
    }

    //删除解密文件
    private static void deleteDecryptFile(String filePath) {
        if (filePath == null || !filePath.contains(FileUtils.ENCRYPT_FILE_EXT)) {
            return;
        }
        File file = new File(filePath + FileUtils.DECRYPT_FILE_EXT);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath
     */
    public static void deleteMd5File(String filePath) {
        File file = new File(FileUtils.DOWNLOAD_PATH + CipherUtils.md5(filePath));
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }


    public static final byte[] input2byte(InputStream inStream) {
        if (inStream == null) {
            return null;
        } else {
            byte[] in2b = null;
            BufferedInputStream in = new BufferedInputStream(inStream);
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            boolean rc = false;

            try {
                int rc1;
                while ((rc1 = in.read()) != -1) {
                    swapStream.write(rc1);
                }

                in2b = swapStream.toByteArray();
            } catch (IOException var9) {
                var9.printStackTrace();
            } finally {
                closeIO(inStream, in, swapStream);
            }

            return in2b;
        }
    }

    public static File uri2File(Activity aty, Uri uri) {
        String[] projection;
        if (SystemTool.getSDKVersion() < 11) {
            projection = new String[]{"_data"};
            Cursor loader1 = aty.managedQuery(uri, projection, null, null, null);
            int cursor1 = loader1.getColumnIndexOrThrow("_data");
            loader1.moveToFirst();
            String column_index1 = loader1.getString(cursor1);
            return new File(column_index1);
        } else {
            projection = new String[]{"_data"};
            CursorLoader loader = new CursorLoader(aty, uri, projection, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow("_data");
            cursor.moveToFirst();
            return new File(cursor.getString(column_index));
        }
    }

    public static void copyFile(File from, File to) {
        if (from != null && from.exists()) {
            if (to != null) {
                FileInputStream is = null;
                FileOutputStream os = null;

                try {
                    is = new FileInputStream(from);
                    if (!to.exists()) {
                        to.createNewFile();
                    }

                    os = new FileOutputStream(to);
                    copyFileFast(is, os);
                } catch (Exception var8) {
                    throw new RuntimeException(FileUtils.class.getClass().getName(), var8);
                } finally {
                    closeIO(is, os);
                }

            }
        }
    }

    public static void copyFileFast(FileInputStream is, FileOutputStream os) throws IOException {
        FileChannel in = is.getChannel();
        FileChannel out = os.getChannel();
        in.transferTo(0L, in.size(), out);
    }

    public static void closeIO(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            Closeable[] var4 = closeables;
            int var3 = closeables.length;

            for (int var2 = 0; var2 < var3; ++var2) {
                Closeable cb = var4[var2];

                try {
                    if (cb != null) {
                        cb.close();
                    }
                } catch (IOException var6) {
                    throw new RuntimeException(FileUtils.class.getClass().getName(), var6);
                }
            }

        }
    }

    public static boolean SaveBitmapToFile(Bitmap bitmap, String filePath) {
        boolean isSuccess = false;
        if (bitmap == null) {
            return isSuccess;
        } else {
            File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }

            BufferedOutputStream out = null;

            try {
                out = new BufferedOutputStream(new FileOutputStream(filePath), 8192);
                isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            } catch (FileNotFoundException var9) {
                var9.printStackTrace();
            } finally {
                closeIO(out);
            }

            return isSuccess;
        }
    }

    /**
     * @param url
     * @return
     */
    public final static Bitmap downloadImage(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
            HttpURLConnection conn;

            conn = (HttpURLConnection) fileUrl.openConnection();

            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String readFile(String filePath) {
        FileInputStream is = null;

        try {
            is = new FileInputStream(filePath);
        } catch (Exception var3) {
            throw new RuntimeException(FileUtils.class.getName() + "readFile---->" + filePath + " not found");
        }

        return inputStream2String(is);
    }

    public static byte[] readFileBytes(File file) {

        BufferedInputStream bis = null;
        byte[] bytIn = null;
        try {
            if (!file.exists()) {
                return null;
            }
            bis = new BufferedInputStream(new FileInputStream(file));
            bytIn = new byte[(int) file.length()];
            bis.read(bytIn);
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytIn;
    }

    public static String readFileFromAssets(Context context, String name) {
        InputStream is = null;

        try {
            is = context.getResources().getAssets().open(name);
        } catch (Exception var4) {
            throw new RuntimeException(FileUtils.class.getName() + ".readFileFromAssets---->" + name + " not found");
        }

        return inputStream2String(is);
    }

    public static void bytesToFile(String filePath, byte[] bytes) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bytes);
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String inputStream2String(InputStream is) {
        if (is == null) {
            return null;
        } else {
            StringBuilder resultSb = null;

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                resultSb = new StringBuilder();

                String len;
                while ((len = br.readLine()) != null) {
                    resultSb.append(len);
                }
            } catch (Exception var7) {
            } finally {
                closeIO(is);
            }

            return resultSb == null ? null : resultSb.toString();
        }
    }

    public static boolean isNoFile(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return !file.exists();
    }

    public static String encryptFile(String filePath) {

        File file = new File(filePath);
        if (!file.exists()) {//大于20MB的不加密
            return null;
        }

        byte[] byteIn = readFileBytes(file);
        if (byteIn == null) {
            return null;
        }

        try {
            Trace.i("devin", "start encrypt");
            long t = System.currentTimeMillis();
            String saveFilePath = filePath + ENCRYPT_FILE_EXT;
            byte[] byteOut = encrypt(byteIn, CRYPT_KEY);
            bytesToFile(saveFilePath, byteOut);
            Trace.i("devin", "end encrypt, time:" + (System.currentTimeMillis() - t));
            return saveFilePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptFile(String filePath) {

        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        byte[] byteIn = readFileBytes(file);
        if (byteIn == null) {
            return null;
        }

        try {
            Trace.i("devin", "start decryptFile");
            long t = System.currentTimeMillis();
            String newFilePath = filePath + DECRYPT_FILE_EXT;
            byte[] byteOut = decrypt(byteIn, CRYPT_KEY);
            bytesToFile(newFilePath, byteOut);
            Trace.i("devin", "end decryptFile, time:" + (System.currentTimeMillis() - t));
            return newFilePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密
     */
    public static byte[] encrypt(byte[] src, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), AES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey);//设置密钥和加密形式
        return cipher.doFinal(src);
    }

    /**
     * 解密
     */
    public static byte[] decrypt(byte[] src, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), AES);//设置加密Key
        cipher.init(Cipher.DECRYPT_MODE, securekey);//设置密钥和解密形式
        return cipher.doFinal(src);
    }

    /**
     * 二行制转十六进制字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }

    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0KB";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(0, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(1, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }


    /**
     * 给定资源Url获取文件的扩展名
     *
     * @param url
     * @return
     */
    public static String parseSuffix(String url) {

        Pattern pattern = Pattern.compile("\\S*[?]\\S*");
        Matcher matcher = pattern.matcher(url);

        String[] spUrl = url.toString().split("/");
        int len = spUrl.length;
        String endUrl = spUrl[len - 1];

        if (matcher.find()) {
            String[] spEndUrl = endUrl.split("\\?");
            return spEndUrl[0].split("\\.")[1];
        }
        return endUrl.split("\\.")[1];
    }


}

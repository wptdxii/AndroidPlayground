package com.wptdxii.androidpractice.widget.recycleradapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 图片压缩并保存处理
 *
 * @author wenlong.lu
 *         <p/>
 *         2015年4月16日下午10:05:49
 */
public class ImageFileUtils {

    // 质量压缩方法
    public static Bitmap compressImage(Bitmap mBitmap) {

        if (null != mBitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                mBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            return BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        }

        return null;

    }

    public static Bitmap readBitmapFromFile(String imagePath, float maxW, float maxH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, newOpts);// 此时返回bm为空

        int width = newOpts.outWidth;
        int height = newOpts.outHeight;

        int scale = 1;
        if (width > height && width > maxW) {// 如果宽度大的话根据宽度固定大小缩放
            scale = (int) (newOpts.outWidth / maxW);
        } else if (width < height && height > maxH) {// 如果高度高的话根据宽度固定大小缩放
            scale = (int) (newOpts.outHeight / maxH);
        }
        if (scale <= 0)
            scale = 1;

        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    public static String bitmaptoString(Bitmap bitmap) { // 将Bitmap转换成Base64字符串
        StringBuffer string = new StringBuffer();
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
            bStream.flush();
            bStream.close();
            byte[] bytes = bStream.toByteArray();
            string.append(Base64.encodeToString(bytes, Base64.NO_WRAP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string.toString();
    }
}

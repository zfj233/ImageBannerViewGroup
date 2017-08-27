package com.zfj.android.imagebanner.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by zfj_ on 2017/8/25.
 */

public class BitmapUtils {
    public static Bitmap qualityCompress(Bitmap bt){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 50;
        bt.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Log.i("wechat", "压缩后图片的大小" + (bm.getByteCount() / 1024 / 1024)
                + "M宽度为" + bm.getWidth() + "高度为" + bm.getHeight()
                + "bytes.length=  " + (bytes.length / 1024) + "KB"
                + "quality=" + quality);
        return bm;
    }
    public static Bitmap sampleCompress(Context context,int id){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), id, options);

        return bm;
    }
}

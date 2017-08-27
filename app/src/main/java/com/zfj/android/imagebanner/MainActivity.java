package com.zfj.android.imagebanner;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.zfj.android.imagebanner.utils.BitmapUtils;
import com.zfj.android.imagebanner.view.ImageBannerFrameLayout;
import com.zfj.android.imagebanner.view.ImageBannerViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageBannerFrameLayout.FrameLayoutListener {

    //private ImageBannerViewGroup mGroup;
    private ImageBannerFrameLayout mGroup;
    private int[] ids = new int[]{
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //需要计算出我们当前手机宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        C.WIDTH = dm.widthPixels;

        mGroup = (ImageBannerFrameLayout) findViewById(R.id.image_group);
        /**
         * 之前的方法：直接获取ImageBannerViewGroup
         * 现在的方法：使用ImageBannerFrameLayout
         */
//        for (int i = 0; i < ids.length; i++) {
//            ImageView iv = new ImageView(this);
//            iv.setImageResource(ids[i]);
//            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            iv.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
//            mGroup.addView(iv);
//        }
//        mGroup.setListener(this);
        List<Bitmap> list = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),ids[i]);
            /**
             * 压缩避免大图片OOM
             */
            Bitmap bitmap = BitmapUtils.sampleCompress(this,ids[i]);
            bitmap = BitmapUtils.qualityCompress(bitmap);
            list.add(bitmap);
            //bitmap.recycle();//异常！
        }
        mGroup.addList(list);
        mGroup.setListener(this);//FrameLayoutListener
    }

    @Override
    public void clickImageIndex(int pos) {
        Toast.makeText(this, " pos = " + pos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

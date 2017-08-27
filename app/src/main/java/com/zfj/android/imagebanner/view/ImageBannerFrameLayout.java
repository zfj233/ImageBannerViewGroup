package com.zfj.android.imagebanner.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zfj.android.imagebanner.C;
import com.zfj.android.imagebanner.R;

import java.util.List;

/**
 * 轮播视图+LinearLayout(圆点)
 * Created by zfj_ on 2017/8/25.
 */

public class ImageBannerFrameLayout extends FrameLayout implements ImageBannerViewGroup.ImageBannerViewGroupListener
    ,ImageBannerViewGroup.ImageBannerListener{
    private ImageBannerViewGroup imageBannerViewGroup;
    private LinearLayout linearLayout;
    /**
     * listener
     */
    private FrameLayoutListener mListener;

    /**
     * get,set listener
     */
    public FrameLayoutListener getListener() {
        return mListener;
    }

    public void setListener(FrameLayoutListener listener) {
        mListener = listener;
    }

    /**
     * 构造方法，初始化ImageBannerViewGroup和LinearLayout
     * @param context
     */
    public ImageBannerFrameLayout(@NonNull Context context) {
        super(context);
        initImageBannerViewGroup();
        initDotLinearLayout();
    }


    public ImageBannerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initImageBannerViewGroup();
        initDotLinearLayout();
    }

    public ImageBannerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initImageBannerViewGroup();
        initDotLinearLayout();
    }

    /**
     * 初始化底部圆点布局
     */
    private void initDotLinearLayout() {
        linearLayout = new LinearLayout(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                , 40);
        linearLayout.setLayoutParams(lp);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);

        linearLayout.setBackgroundColor(Color.RED);

        addView(linearLayout);
        //layout_gravity
        FrameLayout.LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM;
        linearLayout.setLayoutParams(layoutParams);

        //设置透明度：3.0之后我们使用的是setAlpha(),3.0之前使用的是setAlpha(),调用者不同！
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//11
            linearLayout.setAlpha(0.5f);
        } else {//3.0之前
            linearLayout.getBackground().setAlpha(100);
        }
    }

    /**
     * 初始化轮播视图
     */
    private void initImageBannerViewGroup() {
        imageBannerViewGroup = new ImageBannerViewGroup(getContext());
        //参数
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                , FrameLayout.LayoutParams.MATCH_PARENT);
        imageBannerViewGroup.setLayoutParams(lp);
        imageBannerViewGroup.setListener(this);//点击事件
        imageBannerViewGroup.setBannerViewGroupListener(this);//圆点切换
        addView(imageBannerViewGroup);
    }

    /**
     * 添加所有图片到轮播控件
     *
     * @param list
     */
    public void addList(List<Bitmap> list) {
        for (int i = 0; i < list.size(); i++) {
            Bitmap bitmap = list.get(i);
            addBitmapToGroup(bitmap);
            addDotToLinearLayout();//圆点to LinearLayout
        }

    }


    /**
     * 添加单个图片到轮播控件
     *
     * @param bitmap
     */
    private void addBitmapToGroup(Bitmap bitmap) {
        ImageView iv = new ImageView(getContext());
        iv.setImageBitmap(bitmap);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setLayoutParams(new ViewGroup.LayoutParams(C.WIDTH//使用全局变量C.WIDTH
                , ViewGroup.LayoutParams.WRAP_CONTENT));
        imageBannerViewGroup.addView(iv);
    }

    /**
     * 添加圆点到LinearLayout
     */
    private void addDotToLinearLayout() {
        ImageView iv = new ImageView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        iv.setLayoutParams(lp);
        iv.setImageResource(R.drawable.dot_normal);
        linearLayout.addView(iv);
    }

    @Override
    public void selectImage(int index) {
        int count = linearLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView iv = (ImageView) linearLayout.getChildAt(i);
            if(i == index){
                iv.setImageResource(R.drawable.dot_select);
            } else{
                iv.setImageResource(R.drawable.dot_normal);
            }
        }
    }

    @Override
    public void clickImageIndex(int pos) {
        mListener.clickImageIndex(pos);
    }

    /**
     * MainActivity接口
     * 点击事件
     */
    public interface FrameLayoutListener{
        void clickImageIndex(int pos);
    }
}

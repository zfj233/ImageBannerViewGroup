
package com.zfj.android.imagebanner.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 实现图片轮播 核心类
 */
public class ImageBannerViewGroup extends ViewGroup {
    /**
     * 必须要实现的方法：测量-》布局-》绘制
     * <p>
     * 绘制：
     * 我们对于绘制来说，我们是自定义的ViewGroup容器，
     * 针对于容器的绘制，其实就是容器内的子空间的绘制过程
     * 我们只需要调用系统自带的绘制即可。
     * <p>
     * 也就是说对于ViewGroup的绘制过程，我们不需要再重写该方法了
     * 调用系统自带即可
     *
     * @param context
     */
    private int mChildren;
    private int mChildWidth;//子视图宽度
    private int mChildHeight;//子视图高度
    private int x;//第一次按下的位置的横坐标，每一次移动过程中，移动之前的位置横坐标
    private int index = 0;//代表每张图片索引
    private static final String TAG = "ImageBannerViewGroup";

    private Scroller mScroller;
    /**
     * ImageBannerListener
     * 点击事件
     * ImageBannerViewGroupListener
     * 圆点切换
     */
    private ImageBannerListener mListener;
    private ImageBannerViewGroupListener mBannerViewGroupListener;

    /**
     * get set listener
     */
    public ImageBannerViewGroupListener getBannerViewGroupListener() {
        return mBannerViewGroupListener;
    }

    public void setBannerViewGroupListener(ImageBannerViewGroupListener bannerViewGroupListener) {
        mBannerViewGroupListener = bannerViewGroupListener;
    }


    public ImageBannerListener getListener() {
        return mListener;
    }

    public void setListener(ImageBannerListener listener) {
        this.mListener = listener;
    }

    /**
     * 单击事件，利用一个单击变量开关进行判断，在用户离开屏幕的一瞬间
     * 我们去判断变量开关来判断用户的操作是点击还是移动。
     */
    private boolean isClick;//true:点击事件，false:不是点击事件

    public interface ImageBannerListener {
        void clickImageIndex(int pos);//pos当前图片的具体索引值
    }

    /**
     * 底部圆点以及底部圆点切换步骤思路：
     * 1.利用一个自定义继承FrameLayout的布局，利用FrameLayout布局的特性：在同一位置放置不同的View会
     *   最终显示的是最后一个View，我们就可实现底部圆点的布局。
     * 2.我们需要准备 素材，底部圆点的素材。我们可以利用Drawable的功能去实现一个圆点图片的展示。
     * 3.我们需要继承FrameLayout来自定义一个类，在该类的实现过程中，我们去加载我们刚才自定义的
     *   ImageBannerViewGroup核心类和我们需要实现的底部圆点的布局LinearLayout
     */


    /**
     * 自动轮播
     * 采用Timer，TimerTask，Handler三者相结合实现自动轮播。
     * 抽出两个方法控制是否启动自动轮播，我们称之为startAuto(),stopAuto();
     * 那我们在两个方法的控制过程中，我们实际上希望的是自动开启轮播图的开关
     * 我们就需要一个变量参数来作为我们自动开启轮播图的开关，
     * 称之为isAuto(boolean型)
     * true：开启，false：关闭
     */
    private boolean isAuto = true;//默认情况下开启自动轮播
    private Timer mTimer = new Timer();
    private TimerTask mTask;
    private Handler autoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://此时我们需要图片的自动轮播
                    if (++index >= mChildren) {//最后一张图片的话，从第一张图片开始重新滑动
                        index = 0;
                    }

                    scrollTo(mChildWidth * index, 0);//与scrollBy区分！
                    mBannerViewGroupListener.selectImage(index);//通知当前圆点需要绘制
                    break;
            }
        }
    };

    private void startAuto() {
        isAuto = true;
    }

    private void stopAuto() {
        isAuto = false;
    }

    /**
     * 必须实现构造方法
     *
     * @param context
     */
    public ImageBannerViewGroup(Context context) {
        super(context);
        initObj();

    }


    public ImageBannerViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initObj();
    }

    public ImageBannerViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initObj();
    }

    private void initObj() {
        mScroller = new Scroller(getContext());
        /**
         * 自动轮播,初始化Timer，TimerTask
         */
        mTask = new TimerTask() {
            @Override
            public void run() {
                if (isAuto) {//已开启自动轮播
                    autoHandler.sendEmptyMessage(0);
                }
            }
        };
        mTimer.schedule(mTask, 100, 3000);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    /**
     * 继承ViewGroup必须实现onLayout布局
     *
     * @param changed 布局位置发生改变 ：true，未发生改变：false
     * @param l       left
     * @param t       top
     * @param r       right
     * @param b       bottom
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int leftMargin = 0;

            for (int i = 0; i < mChildren; i++) {
                View view = getChildAt(i);
                //t不变
                view.layout(leftMargin, 0, leftMargin + mChildWidth, mChildHeight);
                leftMargin += mChildWidth;
            }
        }
    }


    /**
     * * 事件的传递过程中调用的方法。
     * 1：我们需要调用容器的拦截方法，onInterceptTouchEvent(MotionEvent ev)
     * 针对于该方法我们可以理解为 如果说，该方法返回true，我们自定义的ViewGroup容器就会
     * 处理此次拦截事件，若返回值为false，那么我们自定义的ViewGroup容器将不会接受此次事件的
     * 处理，将会继续向下传递该事件。
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    /**
     * 我们针对于我们自定义的ViewGroup，我们希望我们的ViewGroup容器处理接受事件，
     * 我们的返回值就应该是true，真正处理该事件的方法onTouchEvent方法
     * <p>
     * <p>
     * 用两种方式实现轮播图的手动轮播，
     * 1.利用scrollTo，scrollBy 完成子视图的手动轮播
     * 2.利用scroller对象 完成对轮播图的手动轮播
     * <p>
     * 第一：我们在滑动屏幕图片的过程中其实就是我们自定义ViewGroup
     * 的子视图的移动过程
     * 那么我们只需要知道滑动之前的横坐标和滑动之后的横坐标
     * 此时我们就可以求是我们此过程中滑动的距离，我们在利用scrollBy
     * 方法来时现图片的滑动，所以，此时我们需要求出：移动前，移动后
     * 的横坐标值！
     * <p>
     * 第二：在我们第一次按下的那一瞬间，此时移动前和移动后的值是相等的
     * 就是我们此时按下的一瞬间的那一个点的横坐标的值
     * <p>
     * 第三：我们在不断地滑动过程中，是会不断调用我们的ACTION_MOVE方法
     * 此时我们就应该将移动之前的值和移动之后的值进行保存，以便我们能够算
     * 出能够滑动的距离。
     * <p>
     * 第四：在我们抬起的那一瞬间，我们需要计算出我们此时将要滑动的那张图片
     * 的位置，我们此时需要求得出将要滑动到的那张图片的索引值，
     * 求法：(当前ViewGroup滑动位置+每张图片宽度/2)/我们每一张图片宽度值
     * <p>
     * 此时我们就可以利用scrollTo 滑动到该图片的位置上；
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**
         * 扩展：手势探测GestureDetector
         */

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://表示用户按下一瞬间
                stopAuto();//停止自动轮播
                //优化
                if (!mScroller.isFinished()) {//滑动还未完成
                    mScroller.abortAnimation();//以第二次滑动点击操作为主
                }
                isClick = true;
                x = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE://表示用户按下屏幕上移动的过程
                int moveX = (int) event.getX();
                int distance = moveX - x;
                scrollBy(-distance, 0);//注意用的是scrollBy
                x = moveX;
                isClick = false;
                break;
            case MotionEvent.ACTION_UP://表示用户抬起一瞬间
                /**
                 * 计算index
                 */
                int scrollX = getScrollX();
                index = (scrollX + mChildWidth / 2) / mChildWidth;
                if (index < 0) {//说明此时已经滑动到了最左边，第一张图片
                    index = 0;
                } else if (index > mChildren - 1) {//滑动到了最右边，最后一张图。
                    index = mChildren - 1;
                }
                /**
                 * 判断是否是单击事件
                 */
                if (isClick) {//是点击事件
                    mListener.clickImageIndex(index);
                } else {//不是点击事件

                    //scrollTo(index * mChildWidth, 0);//滑动到第几张图片

                    int dx = index * mChildWidth - scrollX;
                    mScroller.startScroll(scrollX, 0, dx, 0);//利用scroll滑动，dx+scroll
                    postInvalidate();
                    mBannerViewGroupListener.selectImage(index);
                }
                startAuto();//开启自动轮播
                break;
            default:
                break;
        }
        return true;//返回true的目的是告诉我们该ViewGroup容器的父View已经处理好了该事件
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 实现ViewGroup容器，就应该知道所有子视图
         * 测量ViewGroup的宽度和高度，我们必须先测量子视图的宽度和高度之后
         * 才能知道ViewGroup的宽度和高度是多少
         */

        //1.求子视图的个数
        mChildren = getChildCount();//可以知道子视图的个数
        if (0 == mChildren) {
            setMeasuredDimension(0, 0);
        } else {
            //2.测量子视图宽度和高度
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            //此时我们以第一个子视图为基准，也就是我们ViewGroup高度是我们第一个子视图的高度，
            //宽度是我们第一个子视图的宽度*子视图个数
            View view = getChildAt(0);//因为第一个视图绝对存在

            //3.根据子视图宽度和高度求该ViewGroup的宽度和高度
            mChildHeight = view.getMeasuredHeight();
            mChildWidth = view.getMeasuredWidth();
            int width = view.getMeasuredWidth() * mChildren;//宽度是我们所有子视图宽度总和
            setMeasuredDimension(width, mChildHeight);
        }


    }

    public interface ImageBannerViewGroupListener {
        void selectImage(int index);
    }


}

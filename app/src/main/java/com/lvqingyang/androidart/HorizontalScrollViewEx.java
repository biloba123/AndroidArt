package com.lvqingyang.androidart;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/11/15
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class HorizontalScrollViewEx extends ViewGroup {
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private int mLastInterceptX, mLastInterceptY;
    private int mLastX, mLastY;
    private static final String TAG = "HorizontalScrollViewEx";

    public HorizontalScrollViewEx(Context context) {
        this(context, null);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScroller=new Scroller(context);
        mVelocityTracker=VelocityTracker.obtain();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int paddingH=getPaddingLeft()+getPaddingRight();
        int paddingV=getPaddingTop()+getPaddingBottom();

        int childCount=getChildCount();
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        if(childCount==0){
            //无孩子时根据自身padding和MeasureSpec测量
            if (widthMode== MeasureSpec.AT_MOST&&heightMode== MeasureSpec.AT_MOST) {
                setMeasuredDimension(paddingH, paddingV);
            }else if (widthMode== MeasureSpec.AT_MOST){
                setMeasuredDimension(paddingH, height);
            }else if (heightMode== MeasureSpec.AT_MOST){
                setMeasuredDimension(width, paddingV);
            }else {
                setMeasuredDimension(width, height);
            }
        }else {
            int maxHeight=0;
            int widthMeasure=paddingH;
            for (int i = 0; i < childCount; i++) {
                View child=getChildAt(i);
                if (child != null && child.getVisibility() != View.GONE) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                    widthMeasure += lp.leftMargin + lp.rightMargin + child.getMeasuredWidth();
                    maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
                }
            }

            if (widthMode== MeasureSpec.AT_MOST&&heightMode== MeasureSpec.AT_MOST) {
                setMeasuredDimension(widthMeasure, maxHeight+=paddingV);
            }else if (widthMode== MeasureSpec.AT_MOST){
                setMeasuredDimension(widthMeasure, height);
            }else if (heightMode== MeasureSpec.AT_MOST){
                setMeasuredDimension(Math.max(widthMeasure, width), maxHeight+=paddingV);
            }else {
                setMeasuredDimension(Math.max(widthMeasure, width), height);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount=getChildCount();
        int childLeft=getPaddingLeft();
        for (int i = 0; i < childCount; i++) {
            View child=getChildAt(i);
            if (child != null && child.getVisibility() != View.GONE) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                childLeft += lp.leftMargin;
                child.layout(childLeft, getPaddingTop() + lp.topMargin,
                        childLeft + child.getMeasuredWidth(), getPaddingTop() + lp.topMargin + child.getMeasuredHeight());
                childLeft += child.getMeasuredWidth() + lp.rightMargin;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x= (int) ev.getX(), y= (int) ev.getY();
        boolean isIntercept=false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:{
                isIntercept=false;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                if (Math.abs(x-mLastInterceptX)>Math.abs(y-mLastInterceptY)) {
                    isIntercept=true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                isIntercept=true;
                break;
            }
            default:{
                break;
            }
        }

        mLastInterceptX=mLastX=x;
        mLastInterceptY=mLastY=y;
        if (BuildConfig.DEBUG) Log.d(TAG, "onInterceptTouchEvent: "+isIntercept+" "+x+" "+y);
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x= (int) event.getX(), y= (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:{
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                scrollBy(mLastX-x, 0);
                break;
            }
            case MotionEvent.ACTION_UP:{
                break;
            }
            default:{
                break;
            }
        }

        mLastX=x;
        mLastY=y;

        return true;
    }

    private void smoothScroolBy(int dx, int dy){
        mScroller.startScroll(getScrollX(), 0, dx, dy);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }


    // 继承自margin，支持子视图android:layout_margin属性
    public static class LayoutParams extends MarginLayoutParams {


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }


        public LayoutParams(int width, int height) {
            super(width, height);
        }


        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }


        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }


}

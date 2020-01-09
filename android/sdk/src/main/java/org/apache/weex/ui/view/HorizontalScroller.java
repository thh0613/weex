package org.apache.weex.ui.view;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;


import org.apache.weex.common.OnWXScrollListener;

import java.lang.reflect.Field;

/**
 * @Desc: 反射方法拿到HorizontalScrollView 的 OverScroller
 * created by taohuahua on 2019-04-10
 */
public class HorizontalScroller extends OverScroller {
    private WXHorizontalScrollView mTarget;

    public HorizontalScroller(Context context) {
        super(context);
    }

    public HorizontalScroller(Context context, WXHorizontalScrollView target) {
        super(context);
        mTarget = target;
    }

    public HorizontalScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void abortAnimation() {
        super.abortAnimation();
        mTarget.setMode(OnWXScrollListener.IDLE);
    }

    public void initHorizontalScroll(HorizontalScrollView scrollView) {
        try {
            Field mScroller = HorizontalScrollView.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(scrollView, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

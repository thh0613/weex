/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.weex.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import org.apache.weex.common.OnWXScrollListener;
import org.apache.weex.ui.view.gesture.WXGesture;
import org.apache.weex.ui.view.gesture.WXGestureObservable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WXHorizontalScrollView extends HorizontalScrollView implements IWXScroller, WXGestureObservable {

  private WXGesture wxGesture;
  private ScrollViewListener mScrollViewListener;
  private List<ScrollViewListener> mScrollViewListeners;
  private boolean scrollable = true;
  private boolean flingable = true;
  public int mMode = OnWXScrollListener.IDLE;

  public WXHorizontalScrollView(Context context) {
    super(context);
    init();
  }

  private void init() {
    setWillNotDraw(false);
    setOverScrollMode(View.OVER_SCROLL_NEVER);
  }

  public WXHorizontalScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public int getMode() {
    return mMode;
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if (mScrollViewListener != null) {
      mScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
    }
    if (mScrollViewListeners != null) {
      for (ScrollViewListener listener : mScrollViewListeners) {
        listener.onScrollChanged(this, l, t, oldl, oldt);
      }
    }
  }

  public void setScrollViewListener(ScrollViewListener scrollViewListener) {
    this.mScrollViewListener = scrollViewListener;
  }

  @Override
  public void destroy() {

  }

  public void addScrollViewListener(ScrollViewListener scrollViewListener) {
    if (mScrollViewListeners == null) {
      mScrollViewListeners = new CopyOnWriteArrayList<>();
    }
    if (!mScrollViewListeners.contains(scrollViewListener)) {
      mScrollViewListeners.add(scrollViewListener);
    }
  }

  public void removeScrollViewListener(ScrollViewListener scrollViewListener) {
    mScrollViewListeners.remove(scrollViewListener);
  }

  @Override
  public void registerGestureListener(WXGesture wxGesture) {
    this.wxGesture = wxGesture;
  }

  @Override
  public WXGesture getGestureListener() {
    return wxGesture;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    boolean result = super.dispatchTouchEvent(event);
    if (wxGesture != null) {
      result |= wxGesture.onTouch(this, event);
    }
    return result;
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if(!scrollable) {
      return true; // when scrollable is set to false, then eat the touch event
    }
    if (ev.getAction() == MotionEvent.ACTION_MOVE) {
      checkModeChanged(OnWXScrollListener.DRAGGING);
    }

    return super.onTouchEvent(ev);
  }

  public interface ScrollViewListener {
    void onScrollStateChanged(int mode);

    void onScrollChanged(WXHorizontalScrollView scrollView, int x, int y, int oldx, int oldy);
  }

  public boolean isScrollable() {
    return scrollable;
  }

  public void setFlingable(boolean isFlingable) {
    this.flingable = isFlingable;
  }


  public void setScrollable(boolean scrollable) {
    this.scrollable = scrollable;
  }

  @Override
  public void fling(int velocityX) {
    checkModeChanged(OnWXScrollListener.SETTLING);
    super.fling(flingable ? velocityX : velocityX / 1000);
  }

  public void setMode(int mode) {
    checkModeChanged(mode);
  }

  private void checkModeChanged(int newMode) {
    if (mMode != newMode) {
      mMode = newMode;
      if (mScrollViewListener != null) {
        mScrollViewListener.onScrollStateChanged(mMode);
      }
    }
  }

  public Rect getContentFrame() {
    return new Rect(0, 0, computeHorizontalScrollRange(), computeVerticalScrollRange());
  }
}

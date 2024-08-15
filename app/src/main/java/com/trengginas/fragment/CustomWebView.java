package com.trengginas.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class CustomWebView extends WebView {

    public CustomWebView(Context context) {
        super(context);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            // Disallow parent interception of touch events for multi-touch (zoom)
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Allow parent to intercept touch events if the WebView is scrolled to the edge
            getParent().requestDisallowInterceptTouchEvent(false);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        // Enable parent interception of touch events if the WebView is at the edge
        if (l == 0 || l == computeHorizontalScrollRange() - getWidth()) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
    }
}

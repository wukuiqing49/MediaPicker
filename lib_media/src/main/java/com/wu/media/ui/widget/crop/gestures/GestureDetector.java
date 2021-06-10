package com.wu.media.ui.widget.crop.gestures;

import android.view.MotionEvent;

public interface GestureDetector
{
    boolean onTouchEvent(MotionEvent ev);

    boolean isDragging();

    boolean isScaling();

    void setOnGestureListener(OnGestureListener listener);
}
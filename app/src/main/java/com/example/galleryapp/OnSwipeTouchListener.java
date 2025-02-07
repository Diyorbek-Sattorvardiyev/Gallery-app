package com.example.galleryapp;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float deltaX = e2.getX() - e1.getX();
            if (Math.abs(deltaX) > Math.abs(e2.getY() - e1.getY())) {
                if (deltaX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
            }
            return true;
        }
    }

    public void onSwipeLeft() {}
    public void onSwipeRight() {}
}

package com.example.luckybookpreview.ui.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Загит Талипов on 15.11.2016.
 */

public class FlipView extends CurlView {
    private static final float ACCELERATION = 0.65f;
    private static final float MOVEMENT_RATE = 1.5f;
    private static final int MAX_TIP_ANGLE = 60;
    private static final int MAX_TOUCH_MOVE_ANGLE = 15;
    private static final float MIN_MOVEMENT = 4f;
    private float accumulatedAngle = 0f;

    private static final int STATE_INIT = 0;
    private static final int STATE_TOUCH = 1;
    private static final int STATE_AUTO_ROTATE = 2;
    private boolean inFlipAnimation = false;

    public FlipView(Context context) {
        super(context);
    }

    public FlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private static final int MAX_RELEASED_VIEW_SIZE = 1;

    private static final int MSG_SURFACE_CREATED = 1;

 /*   private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_SURFACE_CREATED) {
                contentWidth = 0;
                contentHeight = 0;
                requestLayout();
                return true;
            }
            return false;
        }
    });

    void showFlipAnimation() {
        if (!inFlipAnimation) {
            inFlipAnimation = true;

            requestRender();

            handler.postDelayed(new Runnable() { //use a delayed message to avoid flicker, the perfect solution would be sending a message from the GL thread
                public void run() {
                    if (inFlipAnimation)
                        updateVisibleView(-1);
                }
            }, 100);
        }
    }

    public synchronized boolean handleTouchEvent(MotionEvent event, boolean isOnTouchEvent) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // remember page we started on...
                lastPageIndex = getPageIndexFromAngle(accumulatedAngle);
                lastPosition = orientationVertical ? event.getY() : event.getX();
                return isOnTouchEvent;
            case MotionEvent.ACTION_MOVE:
                float delta = orientationVertical ? (lastPosition - event.getY()) : (lastPosition - event.getX());

                if (Math.abs(delta) > controller.getTouchSlop()) {
                    setState(STATE_TOUCH);
                    forward = delta > 0;
                }
                if (state == STATE_TOUCH) {
                    if (Math.abs(delta) > MIN_MOVEMENT) //ignore small movements
                        forward = delta > 0;

                    controller.showFlipAnimation();

                    float angleDelta;
                    if (orientationVertical)
                        angleDelta = 180 * delta / controller.getContentHeight() * MOVEMENT_RATE;
                    else
                        angleDelta = 180 * delta / controller.getContentWidth() * MOVEMENT_RATE;

                    if (Math.abs(angleDelta) > MAX_TOUCH_MOVE_ANGLE) //prevent large delta when moving too fast
                        angleDelta = Math.signum(angleDelta) * MAX_TOUCH_MOVE_ANGLE;

                    // do not flip more than one page with one touch...
                    if (Math.abs(getPageIndexFromAngle(accumulatedAngle + angleDelta) - lastPageIndex) <= 1) {
                        accumulatedAngle += angleDelta;
                    }

                    //Bounce the page for the first and the last page
                    if (frontCards.getIndex() == maxIndex - 1) { //the last page
                        if (accumulatedAngle > frontCards.getIndex() * 180 + MAX_TIP_ANGLE)
                            accumulatedAngle = frontCards.getIndex() * 180 + MAX_TIP_ANGLE;
                    } else if (accumulatedAngle < -MAX_TIP_ANGLE)
                        accumulatedAngle = -MAX_TIP_ANGLE;

                    int anglePageIndex = getPageIndexFromAngle(accumulatedAngle);

                    if (accumulatedAngle >= 0) {
                        if (anglePageIndex != frontCards.getIndex()) {
                            if (anglePageIndex == frontCards.getIndex() - 1) { //moved to previous page
                                swapCards(); //frontCards becomes the backCards
                                frontCards.resetWithIndex(backCards.getIndex() - 1);
                                controller.flippedToView(anglePageIndex, false);
                            } else if (anglePageIndex == frontCards.getIndex() + 1) { //moved to next page
                                swapCards();
                                backCards.resetWithIndex(frontCards.getIndex() + 1);
                                controller.flippedToView(anglePageIndex, false);
                            } else
                                throw new RuntimeException(AphidLog.format("Inconsistent states: anglePageIndex: %d, accumulatedAngle %.1f, frontCards %d, backCards %d", anglePageIndex, accumulatedAngle, frontCards.getIndex(), backCards.getIndex()));
                        }
                    }

                    lastPosition = orientationVertical ? event.getY() : event.getX();

                    controller.getSurfaceView().requestRender();
                    return true;
                }

                return isOnTouchEvent;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (state == STATE_TOUCH) {
                    if (accumulatedAngle < 0)
                        forward = true;
                    else if (accumulatedAngle > frontCards.getIndex() * 180 && frontCards.getIndex() == maxIndex - 1)
                        forward = false;

                    setState(STATE_AUTO_ROTATE);
                    controller.getSurfaceView().requestRender();
                }
                return isOnTouchEvent;
        }

        return false;
    }
*/
    private int getPageIndexFromAngle(float angle) {
        return ((int) angle) / 180;
    }

    private float getDisplayAngle() {
        return accumulatedAngle % 180;
    }
}

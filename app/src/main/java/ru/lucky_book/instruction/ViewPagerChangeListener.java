package ru.lucky_book.instruction;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ru.lucky_book.R;

/**
 * Created by Загит Талипов on 10.11.2016.
 */

public class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {
    private boolean isAnimateDoing = false;
    View rootStartButton;
    Context mContext;
    int mCount;

    public ViewPagerChangeListener(View rootStartButton, int mCount, Context mContext) {
        this.rootStartButton = rootStartButton;
        this.mCount = mCount;
        this.mContext = mContext;
        rootStartButton.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slid_downn));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == mCount - 1) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slid_up);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    rootStartButton.setVisibility(View.VISIBLE);
                    isAnimateDoing = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rootStartButton.startAnimation(animation);
        } else if (!isAnimateDoing && position < mCount - 1 && rootStartButton.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slid_downn);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimateDoing = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rootStartButton.setVisibility(View.GONE);
                    isAnimateDoing = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rootStartButton.startAnimation(animation);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

package com.rm.rmswitch;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Riccardo Moro on 29/07/2016.
 */
public class RMSwitch extends RelativeLayout implements Checkable, View.OnClickListener {
    private static final String BUNDLE_KEY_CHECKED = "bundle_key_checked";
    private static final String BUNDLE_KEY_SUPER_DATA = "bundle_key_super_data";
    private static final String BUNDLE_KEY_ENABLED = "bundle_key_enabled";
    private static final String BUNDLE_KEY_FORCE_ASPECT_RATIO = "bundle_key_force_aspect_ratio";
    private static final String BUNDLE_KEY_BKG_CHECKED_COLOR = "bundle_key_bkg_checked_color";
    private static final String BUNDLE_KEY_BKG_NOT_CHECKED_COLOR = "bundle_key_bkg_not_checked_color";
    private static final String BUNDLE_KEY_TOGGLE_CHECKED_COLOR = "bundle_key_toggle_checked_color";
    private static final String BUNDLE_KEY_TOGGLE_NOT_CHECKED_COLOR = "bundle_key_toggle_not_checked_color";
    private static final String BUNDLE_KEY_TOGGLE_CHECKED_DRAWABLE_RES = "bundle_key_toggle_checked_drawable_res";
    private static final String BUNDLE_KEY_TOGGLE_NOT_CHECKED_DRAWABLE_RES = "bundle_key_toggle_not_checked_drawable_res";

    private static final int ANIMATION_DURATION = 150;
    private static final float SWITCH_STANDARD_ASPECT_RATIO = 2.2f;

    /**
     * The Toggle view, the only moving part of the switch
     */
    private SquareImageView mImgToggle;
    /**
     * The background image of the switch
     */
    private ImageView mImgBkg;

    /**
     * The switch container Layout
     */
    private RelativeLayout mContainerLayout;

    // View variables
    private List<RMSwitchObserver> mObservers;

    /**
     * The current switch state
     */
    private boolean mIsChecked;

    /**
     * If force aspect ratio or keep the given proportion
     */
    private boolean mForceAspectRatio;

    /**
     * If the view is enabled
     */
    private boolean mIsEnabled;

    /**
     * The switch background color when is checked
     */
    private int mBkgCheckedColor;

    /**
     * The switch background color when is not checked
     */
    private int mBkgNotCheckedColor;

    /**
     * The toggle color when checked
     */
    private int mToggleCheckedColor;

    /**
     * The toggle color when not checked
     */
    private int mToggleNotCheckedColor;

    /**
     * The checked toggle drawable resource
     */
    private int mToggleCheckedDrawableResource;

    /**
     * The not checked toggle drawable resource
     */
    private int mToggleNotCheckedDrawableResource;

    private boolean mMoving = false;


    private static LayoutTransition sLayoutTransition;

    public RMSwitch(Context context) {
        this(context, null);
    }

    public RMSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RMSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Create the layout transition if not already created
        if (sLayoutTransition == null) {
            sLayoutTransition = new LayoutTransition();
            sLayoutTransition.setDuration(ANIMATION_DURATION);
            sLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            sLayoutTransition.setInterpolator(
                    LayoutTransition.CHANGING,
                    new DecelerateInterpolator());
        }

        // Inflate the stock switch view
        ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.switch_view, this, true);

        // Get the sub-views
        mImgToggle = (SquareImageView) findViewById(R.id.rm_switch_view_toggle);
        mImgBkg = (ImageView) findViewById(R.id.rm_switch_view_bkgd);
        mContainerLayout = (RelativeLayout) findViewById(R.id.rm_switch_view_container);

        // Activate AnimateLayoutChanges in both the container and the root layout
        setLayoutTransition(sLayoutTransition);
        mContainerLayout.setLayoutTransition(sLayoutTransition);

        // Set the background
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RMSwitch,
                defStyleAttr, 0);

        try {
            // Get the checked flag
            mIsChecked = typedArray.getBoolean(
                    R.styleable.RMSwitch_checked, false);

            // Keep aspect ratio flag
            mForceAspectRatio = typedArray.getBoolean(
                    R.styleable.RMSwitch_forceAspectRatio, true);

            // If the switch is enabled
            mIsEnabled = typedArray.getBoolean(
                    R.styleable.RMSwitch_enabled, true);


            //Get the background checked and not checked color
            mBkgCheckedColor = typedArray.getColor(
                    R.styleable.RMSwitch_switchBkgCheckedColor,
                    Utils.getDefaultBackgroundColor(context));

            mBkgNotCheckedColor = typedArray.getColor(
                    R.styleable.RMSwitch_switchBkgNotCheckedColor,
                    mBkgCheckedColor);


            //Get the toggle checked and not checked colors
            mToggleCheckedColor = typedArray.getColor(
                    R.styleable.RMSwitch_switchToggleCheckedColor,
                    Utils.getAccentColor(context));

            mToggleNotCheckedColor = typedArray.getColor(
                    R.styleable.RMSwitch_switchToggleNotCheckedColor,
                    Color.WHITE);


            // Get the toggle checked and not checked images
            mToggleCheckedDrawableResource = typedArray.getResourceId(
                    R.styleable.RMSwitch_switchToggleCheckedImage, 0);
            mToggleNotCheckedDrawableResource = typedArray.getResourceId(
                    R.styleable.RMSwitch_switchToggleNotCheckedImage, mToggleCheckedDrawableResource);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            typedArray.recycle();
        }
        // Set the OnClickListener
        setOnClickListener(this);

//        mImgToggle.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int x = (int) event.getRawX();
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                        int newX = x - mContainerLayout.getLeft();
//                        if (newX > mContainerLayout.getRight()) {
//                            newX = mContainerLayout.getRight();
//                        } else if (newX < mContainerLayout.getLeft()) {
//                            newX = mContainerLayout.getLeft();
//                        }
//                        v.setX(newX);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        int halfSize = (mContainerLayout.getRight() - mContainerLayout.getLeft()) / 2;
//                        mIsChecked = x > halfSize;
//                        notifyObservers();
//                        break;
//                }
//                return true;
//            }
//        });

        // Set manually checked flag, update the appearance and change the toggle gravity
        setChecked(mIsChecked);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_SUPER_DATA, super.onSaveInstanceState());

        bundle.putBoolean(BUNDLE_KEY_CHECKED, mIsChecked);
        bundle.putBoolean(BUNDLE_KEY_ENABLED, mIsEnabled);
        bundle.putBoolean(BUNDLE_KEY_FORCE_ASPECT_RATIO, mForceAspectRatio);
        bundle.putInt(BUNDLE_KEY_BKG_CHECKED_COLOR, mBkgCheckedColor);
        bundle.putInt(BUNDLE_KEY_BKG_NOT_CHECKED_COLOR, mBkgNotCheckedColor);
        bundle.putInt(BUNDLE_KEY_TOGGLE_CHECKED_COLOR, mToggleCheckedColor);
        bundle.putInt(BUNDLE_KEY_TOGGLE_NOT_CHECKED_COLOR, mToggleNotCheckedColor);
        bundle.putInt(BUNDLE_KEY_TOGGLE_CHECKED_DRAWABLE_RES, mToggleCheckedDrawableResource);
        bundle.putInt(BUNDLE_KEY_TOGGLE_NOT_CHECKED_DRAWABLE_RES, mToggleNotCheckedDrawableResource);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle prevState = (Bundle) state;

        super.onRestoreInstanceState(prevState.getParcelable(BUNDLE_KEY_SUPER_DATA));

        // Restore the check state notifying the observers
        mIsEnabled = prevState.getBoolean(BUNDLE_KEY_ENABLED, true);
        mForceAspectRatio = prevState.getBoolean(BUNDLE_KEY_FORCE_ASPECT_RATIO, true);
        mBkgCheckedColor = prevState.getInt(BUNDLE_KEY_BKG_CHECKED_COLOR,
                Utils.getDefaultBackgroundColor(getContext()));
        mBkgNotCheckedColor = prevState.getInt(BUNDLE_KEY_BKG_NOT_CHECKED_COLOR,
                mBkgCheckedColor);
        mToggleCheckedColor = prevState.getInt(BUNDLE_KEY_TOGGLE_CHECKED_COLOR,
                Utils.getAccentColor(getContext()));
        mToggleNotCheckedColor = prevState.getInt(BUNDLE_KEY_TOGGLE_NOT_CHECKED_COLOR,
                Color.WHITE);
        mToggleCheckedDrawableResource = prevState
                .getInt(BUNDLE_KEY_TOGGLE_CHECKED_DRAWABLE_RES, 0);
        mToggleNotCheckedDrawableResource = prevState
                .getInt(BUNDLE_KEY_TOGGLE_NOT_CHECKED_DRAWABLE_RES, mToggleCheckedDrawableResource);

        setChecked(prevState.getBoolean(BUNDLE_KEY_CHECKED, false));
        notifyObservers();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mForceAspectRatio) {

            // Set the height depending on the width
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    (int) (MeasureSpec.getSize(widthMeasureSpec) / SWITCH_STANDARD_ASPECT_RATIO),
                    MeasureSpec.getMode(heightMeasureSpec));
        } else {

            // Check that the width is greater than the height, if not, resize and make a square
            if (MeasureSpec.getSize(widthMeasureSpec) < MeasureSpec.getSize(heightMeasureSpec))
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        MeasureSpec.getSize(widthMeasureSpec),
                        MeasureSpec.getMode(heightMeasureSpec));
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // If set to wrap content, apply standard dimensions
        if (widthMode != MeasureSpec.EXACTLY) {
            int standardWith = (int) Utils
                    .convertDpToPixel(
                            getContext(),
                            getResources().getDimension(R.dimen.rm_switch_standard_width));

            // If unspecified or wrap_content where there's more space than the standard,
            // set the standard dimensions
            if ((widthMode == MeasureSpec.UNSPECIFIED) ||
                    (widthMode == MeasureSpec.AT_MOST &&
                            standardWith < MeasureSpec.getSize(widthMeasureSpec)))
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(standardWith, MeasureSpec.EXACTLY);
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            int standardHeight = (int) Utils
                    .convertDpToPixel(
                            getContext(),
                            getResources().getDimension(R.dimen.rm_switch_standard_height));

            // If unspecified or wrap_content where there's more space than the standard,
            // set the standard dimensions
            if ((heightMode == MeasureSpec.UNSPECIFIED) ||
                    (heightMode == MeasureSpec.AT_MOST &&
                            standardHeight < MeasureSpec.getSize(heightMeasureSpec)))
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(standardHeight, MeasureSpec.EXACTLY);
        }

        // Set the margin after all measures have been done
        int calculatedMargin = MeasureSpec.getSize(heightMeasureSpec) > 0 ?
                MeasureSpec.getSize(heightMeasureSpec) / 8 :
                (int) Utils.convertDpToPixel(getContext(), 2);
        ((RelativeLayout.LayoutParams) mImgToggle.getLayoutParams()).setMargins(
                calculatedMargin, calculatedMargin, calculatedMargin, calculatedMargin);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    // Setup programmatically the appearance
    public void setEnabled(boolean enabled) {
        mIsEnabled = enabled;
        setupSwitchAppearance();
    }

    public void setSwitchBkgCheckedColor(@ColorInt int color) {
        mBkgCheckedColor = color;
        setupSwitchAppearance();
    }

    public void setSwitchBkgNotCheckedColor(@ColorInt int color) {
        mBkgNotCheckedColor = color;
        setupSwitchAppearance();
    }

    public void setSwitchToggleCheckedColor(@ColorInt int color) {
        mToggleCheckedColor = color;
        setupSwitchAppearance();
    }

    public void setSwitchToggleNotCheckedColor(@ColorInt int color) {
        mToggleNotCheckedColor = color;
        setupSwitchAppearance();
    }

    public void setSwitchToggleCheckedDrawableRes(@DrawableRes int drawable) {
        mToggleCheckedDrawableResource = drawable;
        setupSwitchAppearance();
    }

    public void setSwitchToggleNotCheckedDrawableRes(@DrawableRes int drawable) {
        mToggleNotCheckedDrawableResource = drawable;
        setupSwitchAppearance();
    }

    public void setForceAspectRatio(boolean forceAspectRatio) {
        mForceAspectRatio = forceAspectRatio;
        setupSwitchAppearance();
    }

    // Get the switch setup
    @ColorInt
    public int getSwitchBkgCheckedColor() {
        return mBkgCheckedColor;
    }

    @ColorInt
    public int getSwitchBkgNotCheckedColor() {
        return mBkgNotCheckedColor;
    }

    @ColorInt
    public int getSwitchToggleCheckedColor() {
        return mToggleCheckedColor;
    }

    @ColorInt
    public int getSwitchToggleNotCheckedColor() {
        return mToggleNotCheckedColor;
    }

    public boolean isForceAspectRation() {
        return mForceAspectRatio;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    @DrawableRes
    public int getSwitchToggleCheckedDrawableRes() {
        return mToggleCheckedDrawableResource;
    }

    @DrawableRes
    public int getSwitchToggleNotCheckedDrawableRes() {
        return mToggleNotCheckedDrawableResource;
    }

    /**
     * Adds an observer to the list {@link #mObservers}
     *
     * @param switchObserver The observer to be added
     */
    public void addSwitchObserver(RMSwitchObserver switchObserver) {
        if (switchObserver == null)
            return;

        if (mObservers == null)
            mObservers = new ArrayList<>();

        mObservers.add(switchObserver);
    }

    /**
     * Searches and removes the passed {@link RMSwitchObserver}
     * from the observers list {@link #mObservers}
     *
     * @param switchObserver The observer to be removed
     */
    public void removeSwitchObserver(RMSwitchObserver switchObserver) {
        if (switchObserver != null &&// Valid RMSwitchObserverPassed
                mObservers != null && mObservers.size() > 0 && // Observers list initialized and not empty
                mObservers.indexOf(switchObserver) >= 0) {// new Observer found in the list
            mObservers.remove(mObservers.indexOf(switchObserver));
        }
    }

    /**
     * Notify all the registered observers
     */
    private void notifyObservers() {
        if (mObservers != null) {
            for (RMSwitchObserver observer : mObservers) {
                observer.onCheckStateChange(mIsChecked);
            }
        }
    }

    /**
     * Removes all the observer from {@link #mObservers}
     */
    public void removeSwitchObservers() {
        if (mObservers != null && mObservers.size() > 0)
            mObservers.clear();
    }

    /**
     * Setup all the switch custom attributes appearance
     */
    public void setupSwitchAppearance() {
        // Create the background drawables
        Drawable bkgDrawable =
                ContextCompat.getDrawable(getContext(), R.drawable.post_bkg2);
//        ((GradientDrawable) bkgDrawable).setColor(
//                mIsChecked
//                        ? mBkgCheckedColor
//                        : mBkgNotCheckedColor);

        // Create the toggle drawables
        Drawable toggleDrawable = mIsChecked
                ? ContextCompat.getDrawable(getContext(), R.drawable.spsr_post_icon)
                : ContextCompat.getDrawable(getContext(), R.drawable.russia_post_icon);

        // Set the background drawable
        if (mImgBkg.getDrawable() != null) {
            // Create the transition for the background
            TransitionDrawable bkgdTransitionDrawable = new TransitionDrawable(new Drawable[]{
                    // If it was a transition drawable, take the last one of it's drawables
                    mImgBkg.getDrawable() instanceof TransitionDrawable ?
                            ((TransitionDrawable) mImgBkg.getDrawable()).getDrawable(1) :
                            mImgBkg.getDrawable(),
                    bkgDrawable
            });
            bkgdTransitionDrawable.setCrossFadeEnabled(true);
            // Set the transitionDrawable and start the animation
            mImgBkg.setImageDrawable(bkgdTransitionDrawable);
            bkgdTransitionDrawable.startTransition(ANIMATION_DURATION);
        } else {
            // No previous background image, just set the new one
            mImgBkg.setImageDrawable(bkgDrawable);
        }

        // Set the toggle image
        if (mImgToggle.getDrawable() != null) {
            // Create the transition for the image of the toggle
            TransitionDrawable toggleTransitionDrawable = new TransitionDrawable(new Drawable[]{
                    // If it was a transition drawable, take the last one of it's drawables
                    mImgToggle.getDrawable() instanceof TransitionDrawable ?
                            ((TransitionDrawable) mImgToggle.getDrawable()).getDrawable(1) :
                            mImgToggle.getDrawable(),
                    toggleDrawable
            });
            toggleTransitionDrawable.setCrossFadeEnabled(true);
            // Set the transitionDrawable and start the animation
            mImgToggle.setImageDrawable(toggleTransitionDrawable);
            toggleTransitionDrawable.startTransition(ANIMATION_DURATION);
        } else {
            // No previous toggle image, just set the new one
            mImgToggle.setImageDrawable(toggleDrawable);
        }

        setAlpha(mIsEnabled ? 1f : 0.6f);
    }

    /**
     * Move the toggle from one side to the other of this view,
     * called AFTER setting the {@link #mIsChecked} variable
     */
    private void changeToggleGravity() {

        LayoutParams toggleParams =
                ((LayoutParams) mImgToggle.getLayoutParams());

        // Add the new alignment rule
        toggleParams.addRule(
                mIsChecked ?
                        RelativeLayout.ALIGN_PARENT_RIGHT :
                        RelativeLayout.ALIGN_PARENT_LEFT);

        // Remove the previous alignment rule
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            // RelativeLayout.LayoutParams.removeRule require api >= 17
            toggleParams.removeRule(
                    mIsChecked ?
                            RelativeLayout.ALIGN_PARENT_LEFT :
                            RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {

            // If API < 17 manually set the previously active rule with anchor 0 to remove it
            if (isChecked()) {
                toggleParams.addRule(ALIGN_PARENT_LEFT, 0);
            } else {
                toggleParams.addRule(ALIGN_PARENT_RIGHT, 0);
            }
        }

        mImgToggle.setLayoutParams(toggleParams);
    }

    // Checkable interface methods
    @Override
    public void setChecked(boolean checked) {
        mIsChecked = checked;

        setupSwitchAppearance();
        changeToggleGravity();
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mIsChecked);
        notifyObservers();
    }

    // OnClick action
    @Override
    public void onClick(View v) {
        if (isEnabled())
            toggle();
    }

    // Public interface to watch the check state change
    public interface RMSwitchObserver {
        void onCheckStateChange(boolean isChecked);
    }
}

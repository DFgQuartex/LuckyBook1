package ru.lucky_book.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.R;

/**
 * Created by histler
 * on 08.09.16 17:44.
 */
public class EnableableLinearLayout extends LinearLayout {
    public EnableableLinearLayout(Context context) {
        this(context,null);
    }

    public EnableableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EnableableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs,defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EnableableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context,attrs,defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EnableableLinearLayout, defStyleAttr, 0);

        setEnabled(a.getBoolean(R.styleable.EnableableLinearLayout_android_enabled, isEnabled()));
        a.recycle();
    }

    @Override
    public int[] onCreateDrawableState(final int extraSpace) {
        int extraSpaces=0;
        List<Integer> extraStatesList=new ArrayList<>();
        if(isEnabled()){
            extraSpaces++;
            extraStatesList.add(android.R.attr.state_enabled);
        }
        if(isSelected()){
            extraSpaces++;
            extraStatesList.add(android.R.attr.state_selected);
        }
        if(extraSpaces>0) {
            int[] extraStates=new int[extraSpaces];
            for(int i=0;i<extraSpaces;i++){
                extraStates[i]=extraStatesList.get(i);
            }
            final int[] drawableState = super.onCreateDrawableState(extraSpace + extraSpaces);
            mergeDrawableStates(drawableState, extraStates);
            return drawableState;
        }else {
            return super.onCreateDrawableState(extraSpace);
        }
    }
}

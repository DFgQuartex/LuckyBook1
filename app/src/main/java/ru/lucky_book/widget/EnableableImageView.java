package ru.lucky_book.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.R;

/**
 * Created by histler
 * on 08.09.16 12:31.
 */
public class EnableableImageView extends AppCompatImageView {
    private static final int[] STATE_ENABLED = {android.R.attr.state_enabled};
    public EnableableImageView(Context context) {
        this(context,null);
    }

    public EnableableImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EnableableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs,defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EnableableImageView, defStyleAttr, 0);

        setEnabled(a.getBoolean(R.styleable.EnableableImageView_android_enabled, isEnabled()));
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

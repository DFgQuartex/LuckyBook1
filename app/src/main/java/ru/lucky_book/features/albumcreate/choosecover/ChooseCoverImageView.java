package ru.lucky_book.features.albumcreate.choosecover;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by demafayz on 25.08.16.
 */
public class ChooseCoverImageView extends ImageView {


    public ChooseCoverImageView(Context context) {
        super(context);
    }

    public ChooseCoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChooseCoverImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        int width = (int) (height * 0.72f);
        setMeasuredDimension(width, height);
    }
}

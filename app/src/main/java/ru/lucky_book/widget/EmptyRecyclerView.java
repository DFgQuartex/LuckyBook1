package ru.lucky_book.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Zahit Talipov on 10.08.2016.
 */
public class EmptyRecyclerView extends RecyclerView {


    @Nullable
    View mViewLoad;

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setViewLoad(View viewLoad) {
        mViewLoad = viewLoad;
    }

    public void checkIfEmpty() {
        if (getAdapter().getItemCount() > 0) {
            hideLoad();
        } else {
            showLoad();
        }
    }

    void showLoad() {
        if (mViewLoad != null)
            mViewLoad.setVisibility(VISIBLE);
    }

    void hideLoad() {

        if (mViewLoad != null)
            mViewLoad.setVisibility(GONE);
    }
}

package ru.lucky_book.entities.spread;

import android.support.annotation.LayoutRes;

import ru.lucky_book.R;

/**
 * Created by histler
 * on 29.08.16 13:52.
 */
public enum PageTemplate {
    SINGLE(R.layout.template_0, 1, 1);
    //TWO_HORIZONTAL(R.layout.template_1, 2, 1),
    //TWO_VERTICAL(R.layout.template_2, 1, 2),
    //FOUR(R.layout.template_3, 2, 2);

    @LayoutRes
    private int layoutResId;
    private int widthCount;
    private int heightCount;

    PageTemplate(@LayoutRes int layoutResId, int widthCount, int heightCount) {
        this.layoutResId = layoutResId;
        this.widthCount = widthCount;
        this.heightCount = heightCount;
    }

    public int getLayoutResId() {
        return layoutResId;
    }

    public int getWidthCount() {
        return widthCount;
    }

    public int getHeightCount() {
        return heightCount;
    }

    public int getImagesCount(){
        return widthCount*heightCount;
    }
}
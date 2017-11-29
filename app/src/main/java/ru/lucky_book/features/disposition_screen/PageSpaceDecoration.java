package ru.lucky_book.features.disposition_screen;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Загит Талипов on 10.11.2016.
 */

public class PageSpaceDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view) + 1;
        outRect.right = 0;
        if ((position % 4 == 1)||(position - 3) % 4 == 0) {
            outRect.left = 13;
        }

        outRect.top = 13;

    }
}

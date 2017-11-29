package ru.lucky_book.features.disposition_screen;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.lucky_book.R;
import ru.lucky_book.features.base.BaseViewHolder;
import ru.lucky_book.features.base.listener.OnItemClickListener;
import ru.lucky_book.features.base.listener.OnItemLongClickListener;

/**
 * Created by Загит Талипов on 10.11.2016.
 */

public class PageViewHolder extends BaseViewHolder {
    public PageViewHolder(View itemView) {
        super(itemView);
    }

    public PageViewHolder(View itemView, OnItemClickListener clickListener) {
        super(itemView, clickListener);
    }

    public PageViewHolder(View itemView, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        super(itemView, clickListener, longClickListener);
    }

    @Override
    protected void initView(View itemView) {
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setSelection(boolean isSelected) {
        selectionHolder.setSelected(isSelected);
        page.setSelected(false);
    }

    @BindView(R.id.page)
    ViewGroup page;
    @BindView(R.id.selection_holder)
    FrameLayout selectionHolder;
    @BindView(R.id.pages_num)
    TextView pagesNum;


}

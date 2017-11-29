package ru.lucky_book.features.spreads;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.lucky_book.R;
import ru.lucky_book.features.base.BaseViewHolder;
import ru.lucky_book.features.base.listener.OnItemClickListener;

/**
 * Created by histler
 * on 29.08.16 15:48.
 */
public class SpreadViewHolder extends BaseViewHolder {
    public ViewGroup leftPage;
    public ViewGroup rightPage;
    public TextView pageNum;
    private View selectionHolder;


    public SpreadViewHolder(View itemView, OnItemClickListener clickListener) {
        super(itemView, clickListener);
    }

    @Override
    protected void initView(View itemView) {
        leftPage= (ViewGroup) itemView.findViewById(R.id.left_page);
        rightPage= (ViewGroup) itemView.findViewById(R.id.right_page);
        pageNum= (TextView) itemView.findViewById(R.id.pages_num);
        selectionHolder=itemView.findViewById(R.id.selection_holder);
    }

    @Override
    public void setSelection(boolean isSelected) {
        selectionHolder.setSelected(isSelected);
        leftPage.setSelected(false);
        rightPage.setSelected(false);
    }
}

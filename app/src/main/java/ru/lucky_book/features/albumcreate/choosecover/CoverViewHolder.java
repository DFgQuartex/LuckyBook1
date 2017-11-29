package ru.lucky_book.features.albumcreate.choosecover;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.lucky_book.R;
import ru.lucky_book.features.base.BaseViewHolder;
import ru.lucky_book.features.base.listener.OnItemClickListener;

/**
 * Created by histler
 * on 01.09.16 10:40.
 */
public class CoverViewHolder extends BaseViewHolder {
    public ImageView icon;
    public TextView title;

    public CoverViewHolder(View itemView, OnItemClickListener clickListener) {
        super(itemView, clickListener);
    }

    @Override
    protected void initView(View itemView) {
        icon = (ImageView) itemView.findViewById(R.id.ivIcon);
        title = (TextView) itemView.findViewById(R.id.tvTitle);
    }
}

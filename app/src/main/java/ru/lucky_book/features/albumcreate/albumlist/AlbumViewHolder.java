package ru.lucky_book.features.albumcreate.albumlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.lucky_book.R;
import ru.lucky_book.features.base.BaseViewHolder;
import ru.lucky_book.features.base.listener.OnItemClickListener;

/**
 * Created by histler
 * on 01.09.16 10:31.
 */
public class AlbumViewHolder extends BaseViewHolder {
    public ImageView cover;
    public TextView title;

    public AlbumViewHolder(View itemView, OnItemClickListener clickListener) {
        super(itemView, clickListener);
    }

    @Override
    protected void initView(View itemView) {
        cover = (ImageView) itemView.findViewById(R.id.ivAlbumIcon);
        title = (TextView) itemView.findViewById(R.id.tvTitle);
    }
}

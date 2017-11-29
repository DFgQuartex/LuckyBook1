package ru.lucky_book.features.imageselector;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.lucky_book.R;
import ru.lucky_book.features.base.BaseViewHolder;
import ru.lucky_book.features.base.listener.OnItemClickListener;
import ru.lucky_book.features.base.listener.OnItemLongClickListener;

/**
 * Created by histler
 * on 15.09.16 16:55.
 */
public class ImageViewHolder extends BaseViewHolder {

    public ImageView picture;
    View mask;
    TextView selectIndex;
    ImageView sizeTypeMarker;
    View viewRoot;
    View shadeView;

    public ImageViewHolder(View itemView, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        super(itemView, clickListener, longClickListener);
        viewRoot = itemView;
    }

    @Override
    protected void initView(View itemView) {
        picture = (ImageView) itemView.findViewById(R.id.image);
        mask = itemView.findViewById(R.id.mask);
        selectIndex = (TextView) itemView.findViewById(R.id.select_index);
        sizeTypeMarker = (ImageView) itemView.findViewById(R.id.size_type);
        shadeView = itemView.findViewById(R.id.shade_view);
    }
}

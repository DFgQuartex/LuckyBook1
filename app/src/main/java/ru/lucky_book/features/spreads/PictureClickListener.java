package ru.lucky_book.features.spreads;

import android.widget.ImageView;

import ru.lucky_book.entities.spread.Page;

/**
 * Created by Badr
 * on 04.09.2016 1:39.
 */
public interface PictureClickListener {
    void onPictureClick(ImageView view, Page page, int picturePosition);
}

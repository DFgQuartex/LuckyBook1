package ru.lucky_book.features.instagram;

import ru.lucky_book.data.insta.MediasInsta;
import ru.lucky_book.features.base.MvpView;

/**
 * Created by Загит Талипов on 29.12.2016.
 */

public interface InstagramView extends MvpView {

    void showMedias(MediasInsta mediasInsta);
}

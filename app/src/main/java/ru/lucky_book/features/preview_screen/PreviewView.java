package ru.lucky_book.features.preview_screen;

import ru.lucky_book.data.Price;
import ru.lucky_book.features.base.MvpView;

/**
 * Created by Загит Талипов on 11.11.2016.
 */

public interface PreviewView extends MvpView {

    void showPrice(Price price);

}

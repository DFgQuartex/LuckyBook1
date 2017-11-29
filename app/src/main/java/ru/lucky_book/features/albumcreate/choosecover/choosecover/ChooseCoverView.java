package ru.lucky_book.features.albumcreate.choosecover.choosecover;

import java.util.List;

import ru.lucky_book.features.albumcreate.choosecover.ChooseCoverItem;
import ru.lucky_book.features.base.MvpView;

/**
 * Created by Загит Талипов on 07.11.2016.
 */

public interface ChooseCoverView extends MvpView {
    public void showListCover(List<ChooseCoverItem> chooseCoverItems);
}

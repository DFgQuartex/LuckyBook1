package ru.lucky_book.features.albumcreate.choosecover.choosecover;

import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.data.Cover;
import ru.lucky_book.dataapi.DataManager;
import ru.lucky_book.features.albumcreate.choosecover.ChooseCoverItem;
import ru.lucky_book.features.base.BasePresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Загит Талипов on 07.11.2016.
 */

public class ChooseCoverPresenter extends BasePresenter<ChooseCoverView> {

    DataManager dataManager;


    public ChooseCoverPresenter() {
        dataManager = DataManager.getInstance();
    }

    public void loadListCover() {
        dataManager.listObservableCover()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(covers -> {
                    dataManager.saveCoversLocal(covers);
                    return covers;
                })
                .map(covers -> {
                    List<ChooseCoverItem> chooseCoverItems = new ArrayList<>();
                    for (Cover cover : covers) {
                        chooseCoverItems.add(new ChooseCoverItem(cover.getThumb(), cover.getName(), cover.getUrl(),cover.getId()));
                    }
                    return chooseCoverItems;
                })
                .subscribe(getMvpView()::showListCover, getMvpView()::showError);
    }
}

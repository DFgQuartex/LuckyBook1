package ru.lucky_book.features.albumcreate.choosecover.choosesubcover;

import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.data.SubCover;
import ru.lucky_book.dataapi.DataManager;
import ru.lucky_book.features.albumcreate.choosecover.ChooseCoverItem;
import ru.lucky_book.features.albumcreate.choosecover.choosecover.ChooseCoverView;
import ru.lucky_book.features.base.BasePresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Загит Талипов on 07.11.2016.
 */

public class ChooseSubCoverPresenter extends BasePresenter<ChooseCoverView> {
    DataManager dataManager;

    public ChooseSubCoverPresenter() {
        dataManager = DataManager.getInstance();
    }

    public void loadListCover(String path, int id) {
        dataManager.listObservableSubCover(path, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(subCovers -> {
                    dataManager.saveSubCoversLocal(id, subCovers);
                    return subCovers;
                })
                .map(covers -> {
                    List<ChooseCoverItem> chooseCoverItems = new ArrayList<ChooseCoverItem>();
                    for (SubCover cover : covers) {
                        chooseCoverItems.add(new ChooseCoverItem(cover.getThumb(), cover.getOriginal(), cover.getId()));
                    }
                    return chooseCoverItems;
                })
                .subscribe(getMvpView()::showListCover, getMvpView()::showError);
    }
}

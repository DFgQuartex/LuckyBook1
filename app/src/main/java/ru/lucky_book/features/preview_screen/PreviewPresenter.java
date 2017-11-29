package ru.lucky_book.features.preview_screen;

import ru.lucky_book.dataapi.DataManager;
import ru.lucky_book.features.base.BasePresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Загит Талипов on 11.11.2016.
 */

public class PreviewPresenter extends BasePresenter<PreviewView> {


    DataManager dataManager;

    public PreviewPresenter() {
        dataManager = DataManager.getInstance();
    }

    public void calculateTotalCoast(int count, String promo) {
        dataManager.getPrice(count, promo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::showPrice, getMvpView()::showError);
    }
}

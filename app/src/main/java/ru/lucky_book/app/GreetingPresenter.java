package ru.lucky_book.app;

import ru.lucky_book.dataapi.DataManager;
import ru.lucky_book.features.base.BasePresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Загит Талипов on 09.11.2016.
 */

public class GreetingPresenter extends BasePresenter<GreetingView> {

    DataManager dataManager;

    public GreetingPresenter() {
        this.dataManager = DataManager.getInstance();
    }

    public void checkPromoCode(String code) {
        dataManager.checkPromoCode(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::showCheckPromoCodeResult, getMvpView()::showError);
    }
}

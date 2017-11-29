package ru.lucky_book.features.instagram;

import android.support.annotation.NonNull;

import ru.lucky_book.dataapi.DataManager;
import ru.lucky_book.features.base.BasePresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Загит Талипов on 29.12.2016.
 */

public class InstagramPresenter extends BasePresenter<InstagramView> {

    DataManager mDataManager;
    private static final String DEFAULT_LIMIT = "20";

    public InstagramPresenter() {
        mDataManager = DataManager.getInstance();
    }

    public void getMedias(@NonNull String code, String maxId) {
        mDataManager.getMedias(code, maxId, DEFAULT_LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::showMedias, getMvpView()::showError);
    }
}

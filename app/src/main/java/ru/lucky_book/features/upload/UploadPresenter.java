package ru.lucky_book.features.upload;

import ru.lucky_book.data.OrderLink;
import ru.lucky_book.dataapi.DataManager;
import ru.lucky_book.features.base.BasePresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DemaWork on 21.04.2017.
 */

public class UploadPresenter extends BasePresenter<UploadView> {

    DataManager mDataManager;
    public UploadPresenter() {
        mDataManager = DataManager.getInstance();
    }

    public void sendPdfLink(OrderLink orderLink) {
        mDataManager.sendOrderLink(orderLink)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::resultSendOrderLink, getMvpView()::showError);
    }
}

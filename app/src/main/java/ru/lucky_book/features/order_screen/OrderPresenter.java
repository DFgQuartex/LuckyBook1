package ru.lucky_book.features.order_screen;

import ru.lucky_book.data.Order;
import ru.lucky_book.dataapi.DataManager;
import ru.lucky_book.features.base.BasePresenter;
import ru.lucky_book.network.SpsrApi;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Загит Талипов on 11.11.2016.
 */

public class OrderPresenter extends BasePresenter<OrderView> {

    DataManager mDataManager;
    private SpsrApi mApi;
    Subscription mSubscribe;
    public static String PAYMENT_EXCEPTION = "PAYMENT_EXCEPTION";

    public OrderPresenter() {
        mDataManager = DataManager.getInstance();
    }


    public void sendSuccessfulOrder(Order order, String promo) {
        mDataManager.sendSuccessfulOrder(order, promo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::resultSendOrder, getMvpView()::showError);
    }

    public void sendPreOrder(Order order, String promo) {
        mDataManager.sendPreOrder(order, promo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::resultSendPreOrder, throwable -> {
                    getMvpView().showError(new Exception(PAYMENT_EXCEPTION));
                });
    }

    public void getNewIdForOrder() {
        mDataManager.getNewIdForOrder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::resultGenerateOrderId, getMvpView()::showError);
    }

    public void findCity(String city) {
        if (mSubscribe != null)
            mSubscribe.unsubscribe();
        mSubscribe = mDataManager.findCity(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::showDeliveryPrice, getMvpView()::showError);
    }
}

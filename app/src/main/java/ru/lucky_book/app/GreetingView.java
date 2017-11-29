package ru.lucky_book.app;

import ru.lucky_book.data.PromoCode;
import ru.lucky_book.features.base.MvpView;

/**
 * Created by Загит Талипов on 09.11.2016.
 */

public interface GreetingView extends MvpView {
    void showCheckPromoCodeResult(PromoCode promoCode);
}

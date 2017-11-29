package ru.lucky_book.features.base;

/**
 * Created by Zahit Talipov on 08.07.2016.
 */
public interface MvpPresenter<V extends MvpView> {
    void attachView(V mvpView);
    void detachView();
}

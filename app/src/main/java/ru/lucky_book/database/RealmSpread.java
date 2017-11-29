package ru.lucky_book.database;

import io.realm.RealmObject;

/**
 * Created by Badr
 * on 01.09.2016 0:19.
 */
public class RealmSpread extends RealmObject {
    private RealmPage left;
    private RealmPage right;

    public RealmPage getLeft() {
        return left;
    }

    public void setLeft(RealmPage left) {
        this.left = left;
    }

    public RealmPage getRight() {
        return right;
    }

    public void setRight(RealmPage right) {
        this.right = right;
    }
}

package ru.lucky_book.data;

import android.support.annotation.DrawableRes;

/**
 * Created by Загит Талипов on 10.11.2016.
 */

public class Instruction {

    public static final int TYPE_FIRST = 1;
    public static final int TYPE_LAST = 2;
    public static final int TYPE_MEDIUM = 3;
    String label;
    @DrawableRes
    int image;

    public int getType() {
        return mType;
    }

    int mType;

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    String label2;

    public Instruction(String label, int image) {
        this.label = label;
        this.image = image;
    }

    public String getLabel() {

        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Instruction setType(int type) {
        mType = type;
        return this;
    }
}

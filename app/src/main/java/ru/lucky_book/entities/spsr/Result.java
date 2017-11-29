package ru.lucky_book.entities.spsr;

import org.simpleframework.xml.Attribute;

public class Result {

    @Attribute(name = "RC")
    private int mCode;

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }
}

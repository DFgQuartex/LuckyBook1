package ru.lucky_book.database;

import java.util.Date;

import io.realm.RealmObject;

public class RealmLogin extends RealmObject {

    private String mSid;
    private Date mLoginDate;

    public String getSid() {
        return mSid;
    }

    public void setSid(String sid) {
        mSid = sid;
    }

    public Date getLoginDate() {
        return mLoginDate;
    }

    public void setLoginDate(Date loginDate) {
        mLoginDate = loginDate;
    }
}

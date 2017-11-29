package ru.lucky_book.database;

import io.realm.RealmObject;

/**
 * Created by demafayz on 25.08.16.
 */
public class RealmAppConfigs extends RealmObject {
    private String googleLogin;

    public String getGoogleLogin() {
        return googleLogin;
    }

    public void setGoogleLogin(String googleLogin) {
        this.googleLogin = googleLogin;
    }
}

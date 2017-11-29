package ru.lucky_book.entities.instagram;

import com.google.gson.annotations.SerializedName;

public class InstagramLoginResponse {

    @SerializedName("status")
    private String mStatus;

    @SerializedName("username")
    private String mUsername;

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }
}

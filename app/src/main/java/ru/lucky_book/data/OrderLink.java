package ru.lucky_book.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Загит Талипов on 17.11.2016.
 */

public class OrderLink {
    @SerializedName("common_id")
    int commonId;

    public OrderLink(int commonId, String link) {
        this.commonId = commonId;
        this.link = link;
    }

    public String getLink() {

        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getCommonId() {
        return commonId;
    }

    public void setCommonId(int commonId) {
        this.commonId = commonId;
    }

    String link;
}

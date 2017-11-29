package ru.lucky_book.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Загит Талипов on 09.11.2016.
 */

public class PromoCode implements Parcelable{

    private String name;
    private Integer discount;
    private String skin;

    protected PromoCode(Parcel in) {
        name = in.readString();
        skin = in.readString();
        maxPage = in.readInt();
        skinId = in.readInt();
        begin = in.readString();
        end = in.readString();
        status = in.readString();
    }

    public static final Creator<PromoCode> CREATOR = new Creator<PromoCode>() {
        @Override
        public PromoCode createFromParcel(Parcel in) {
            return new PromoCode(in);
        }

        @Override
        public PromoCode[] newArray(int size) {
            return new PromoCode[size];
        }
    };

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    @SerializedName("max_page")
    private int maxPage;

    public int getSkinId() {
        return skinId;
    }

    public void setSkinId(int skinId) {
        this.skinId = skinId;
    }

    @SerializedName("skin_id")
    private int skinId;
    private String begin;
    private String end;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The discount
     */
    public Integer getDiscount() {
        return discount;
    }

    /**
     * @param discount The discount
     */
    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    /**
     * @return The skin
     */
    public String getSkin() {
        return skin;
    }

    /**
     * @param skin The skin
     */
    public void setSkin(String skin) {
        this.skin = skin;
    }

    /**
     * @return The begin
     */
    public String getBegin() {
        return begin;
    }

    /**
     * @param begin The begin
     */
    public void setBegin(String begin) {
        this.begin = begin;
    }

    /**
     * @return The end
     */
    public String getEnd() {
        return end;
    }

    /**
     * @param end The end
     */
    public void setEnd(String end) {
        this.end = end;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(skin);
        parcel.writeInt(maxPage);
        parcel.writeInt(skinId);
        parcel.writeString(begin);
        parcel.writeString(end);
        parcel.writeString(status);
    }
}

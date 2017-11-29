package ru.lucky_book.entities.facebook;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import ru.lucky_book.entities.SocialImage;

public class Photo implements SocialImage {

    @SerializedName("id")
    private String mId;

    @SerializedName("width")
    private int mWidth;

    @SerializedName("height")
    private int mHeight;

    @SerializedName("source")
    private String mSource;

    @SerializedName("created_time")
    private Date mCreatedTime;

    @SerializedName("images")
    private List<Image> mImages;

    @SerializedName("picture")
    private String mPicture;

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getSource() {
        Image imageMax = null;
        for (Image image : mImages) {
            if (imageMax == null)
                imageMax = image;
            else if (imageMax.mHeight < image.mHeight)
                imageMax = image;
        }
        return imageMax.mSource;
    }

    @Override
    public String getThumbnail() {
        return mPicture;
    }

    @Override
    public long getDate() {
        return mCreatedTime.getTime();
    }

    @Override
    public int getWidth() {
        Image imageMax = null;
        for (Image image : mImages) {
            if (imageMax == null)
                imageMax = image;
            else if (imageMax.mHeight < image.mHeight)
                imageMax = image;
        }
        return imageMax.mWidth;
    }

    @Override
    public int getHeight() {
        Image imageMax = null;
        for (Image image : mImages) {
            if (imageMax == null)
                imageMax = image;
            else if (imageMax.mHeight < image.mHeight)
                imageMax = image;
        }
        return imageMax.mHeight;
    }

    @Override
    public boolean isVideo() {
        return false;
    }

    public static class Image {

        @SerializedName("width")
        private int mWidth;

        @SerializedName("height")
        private int mHeight;

        @SerializedName("source")
        private String mSource;
    }
}

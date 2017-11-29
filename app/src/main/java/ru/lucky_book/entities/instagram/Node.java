package ru.lucky_book.entities.instagram;

import com.google.gson.annotations.SerializedName;

import ru.lucky_book.entities.SocialImage;

public class Node implements SocialImage {

    @SerializedName("id")
    private String mId;

    @SerializedName("thumbnail_src")
    private String mThumbnailSrc;

    @SerializedName("display_src")
    private String mDisplaySrc;

    @SerializedName("dimensions")
    private Dimensions mDimensions;

    @SerializedName("is_video")
    private boolean mVideo;

    @SerializedName("date")
    private long mDate;

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getSource() {
        return mDisplaySrc;
    }

    @Override
    public String getThumbnail() {
        return mThumbnailSrc;
    }

    @Override
    public long getDate() {
        return mDate;
    }

    @Override
    public int getWidth() {
        return mDimensions.mWidth;
    }

    @Override
    public int getHeight() {
        return mDimensions.mHeight;
    }

    @Override
    public boolean isVideo() {
        return mVideo;
    }

    public static class Dimensions {

        @SerializedName("width")
        private int mWidth;

        @SerializedName("height")
        private int mHeight;

    }
}

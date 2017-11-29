package ru.lucky_book.entities.facebook;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoResponse {

    @SerializedName("data")
    private List<Photo> mPhotos;

    @SerializedName("paging")
    private Paging mPaging;

    public Paging getPaging() {
        return mPaging;
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    public static class Paging {

        @SerializedName("cursors")
        private Cursors mCursors;

        public Cursors getCursors() {
            return mCursors;
        }

        public void setCursors(Cursors cursors) {
            mCursors = cursors;
        }

        public static class Cursors {

            @SerializedName("before")
            private String mBefore;

            @SerializedName("after")
            private String mAfter;

            public String getBefore() {
                return mBefore;
            }

            public void setBefore(String before) {
                mBefore = before;
            }

            public String getAfter() {
                return mAfter;
            }

            public void setAfter(String after) {
                mAfter = after;
            }
        }
    }
}

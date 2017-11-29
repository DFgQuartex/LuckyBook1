package ru.lucky_book.entities.instagram;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstagramMediaResponseBody {

    @SerializedName("user")
    private User mUser;

    public User getUser() {
        return mUser;
    }

    public static class User {

        @SerializedName("media")
        private Media mMedia;

        public Media getMedia() {
            return mMedia;
        }

        public static class Media {

            @SerializedName("page_info")
            private PageInfo mPageInfo;

            @SerializedName("nodes")
            private List<Node> mNodes;

            public PageInfo getPageInfo() {
                return mPageInfo;
            }

            public List<Node> getNodes() {
                return mNodes;
            }

            public static class PageInfo {

                @SerializedName("has_previous_page")
                private boolean mPreviousPage;

                @SerializedName("has_next_page")
                private boolean mNextPage;

                @SerializedName("end_cursor")
                private String mEndCursor;

                public boolean hasPreviousPage() {
                    return mPreviousPage;
                }

                public boolean hasNextPage() {
                    return mNextPage;
                }

                public String getEndCursor() {
                    return mEndCursor;
                }

            }
        }
    }
}

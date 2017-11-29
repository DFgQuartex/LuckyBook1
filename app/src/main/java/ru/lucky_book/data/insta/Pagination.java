
package ru.lucky_book.data.insta;


import com.google.gson.annotations.SerializedName;

public class Pagination {

    private String nextUrl;
    @SerializedName("next_max_id")
    private String nextMaxId;

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    public String getNextMaxId() {
        return nextMaxId;
    }

    public void setNextMaxId(String nextMaxId) {
        this.nextMaxId = nextMaxId;
    }

}

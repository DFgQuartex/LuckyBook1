
package ru.lucky_book.data.insta;

import java.util.List;

import ru.lucky_book.entities.SocialImage;

public class MediasInsta{

    private Pagination pagination;
    private Meta meta;
    private List<Datum> data = null;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}

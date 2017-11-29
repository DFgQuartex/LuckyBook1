package ru.lucky_book.database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.lucky_book.data.Cover;

/**
 * Created by Загит Талипов on 18.01.2017.
 */

public class RealmCoverAlbum extends RealmObject {

    @PrimaryKey
    int id;
    public String name;
    public String pathThumb;
    public String url;
    public int priority;
    public RealmList<RealmSubCoverAlbum> mSubcoverAlbumList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathThumb() {
        return pathThumb;
    }

    public void setPathThumb(String pathThumb) {
        this.pathThumb = pathThumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public RealmList<RealmSubCoverAlbum> getSubcoverAlbumList() {
        return mSubcoverAlbumList;
    }

    public void setSubcoverAlbumList(RealmList<RealmSubCoverAlbum> subcoverAlbumList) {
        mSubcoverAlbumList = subcoverAlbumList;
    }

    public Cover getCover(){
        Cover cover = new Cover();
        cover.setId(id);
        cover.setName(name);
        cover.setPriority(priority);
        cover.setThumb(pathThumb);
        return cover;
    }


    public void init(Cover cover) {
        name = cover.getName();
        pathThumb = cover.getThumb();
        url = cover.getUrl();
        priority = cover.getPriority();
    }
}

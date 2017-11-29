package ru.lucky_book.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.lucky_book.data.SubCover;

/**
 * Created by Загит Талипов on 18.01.2017.
 */

 public class RealmSubCoverAlbum extends RealmObject {

    @PrimaryKey
    public int id;
    public int priority;
    public String pathThumb;
    public String pathOriginal;

    public String getPathOriginal() {
        return pathOriginal;
    }

    public void setPathOriginal(String pathOriginal) {
        this.pathOriginal = pathOriginal;
    }

    public String getPathThumb() {
        return pathThumb;
    }

    public void setPathThumb(String pathThumb) {
        this.pathThumb = pathThumb;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public SubCover getCover(){
        SubCover cover = new SubCover();
        cover.setId(id);
        cover.setPriority(priority);
        cover.setThumb(pathThumb);
        cover.setOriginal(pathOriginal);
        return cover;
    }

    public void init(SubCover cover) {
        priority = cover.getPriority();
        pathThumb = cover.getThumb();
        pathOriginal = cover.getOriginal();
    }
}

package ru.lucky_book.database;

import io.realm.RealmObject;

/**
 * Created by Badr
 * on 01.09.2016 0:19.
 */
public class RealmPicture extends RealmObject {
    private String path;
    private int origWidth;
    private int origHeight;

    /*PictureMatrixState*/
    private float x;
    private float y;
    private float zoom;
    private float rotation;

    /*PictureViewState*/
    private int viewportW;
    private int viewportH;
    private int imageW;
    private int imageH;

    private String filter;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOrigWidth() {
        return origWidth;
    }

    public void setOrigWidth(int origWidth) {
        this.origWidth = origWidth;
    }

    public int getOrigHeight() {
        return origHeight;
    }

    public void setOrigHeight(int origHeight) {
        this.origHeight = origHeight;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public int getViewportW() {
        return viewportW;
    }

    public void setViewportW(int viewportW) {
        this.viewportW = viewportW;
    }

    public int getViewportH() {
        return viewportH;
    }

    public void setViewportH(int viewportH) {
        this.viewportH = viewportH;
    }

    public int getImageW() {
        return imageW;
    }

    public void setImageW(int imageW) {
        this.imageW = imageW;
    }

    public int getImageH() {
        return imageH;
    }

    public void setImageH(int imageH) {
        this.imageH = imageH;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}

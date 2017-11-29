package ru.lucky_book.entities.spread;

import org.insta.InstaFilter;

import java.io.Serializable;

/**
 * Created by histler
 * on 29.08.16 14:03.
 */
public class Picture implements Serializable{
    private String path;
    private int origWidth;
    private int origHeight;
    private Class<? extends InstaFilter> filter;
    private PictureMatrixState matrixState;
    private PictureViewState viewState;

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

    public PictureMatrixState getMatrixState() {
        return matrixState;
    }

    public void setMatrixState(PictureMatrixState state) {
        this.matrixState = state;
    }

    public PictureViewState getViewState() {
        return viewState;
    }

    public void setViewState(PictureViewState viewState) {
        this.viewState = viewState;
    }

    public Class<? extends InstaFilter> getFilter() {
        return filter;
    }

    public void setFilter(Class<? extends InstaFilter> filter) {
        this.filter = filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Picture picture = (Picture) o;

        return path != null ? path.equals(picture.path) : picture.path == null;

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}

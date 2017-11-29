package ru.lucky_book.entities.spread;

import com.alexvasilkov.gestures.Settings;

import java.io.Serializable;

/**
 * Created by Badr
 * on 31.08.2016 22:39.
 */
public class PictureViewState implements Serializable {

    /**
     * Viewport area.
     */
    private int viewportW;
    private int viewportH;
    /**
     * Image size.
     */
    private int imageW;
    private int imageH;

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

    public void fromSettings(Settings settings){
        imageW=settings.getImageW();
        imageH=settings.getImageH();
        viewportW=settings.getViewportW();
        viewportH=settings.getViewportH();
    }

    public Settings toSettings(){
        Settings settings=new Settings();
        settings.setImage(imageW,imageH);
        settings.setViewport(viewportW,viewportH);
        return settings;
    }
}

package ru.lucky_book.entities.spread;

import java.io.Serializable;

/**
 * Created by histler
 * on 29.08.16 14:03.
 */
public class Page implements Serializable {
    private PageTemplate template = PageTemplate.SINGLE;
    private Picture[] pictures;

    public PageTemplate getTemplate() {
        return template;
    }

    public void setTemplate(PageTemplate template) {
        this.template = template;
        if (pictures != null && pictures.length != template.getImagesCount()) {
            Picture[] temp = pictures;
            pictures = new Picture[template.getImagesCount()];
            for (int i = 0, size = Math.min(temp.length, pictures.length); i < size; i++) {
                pictures[i] = temp[i];
            }
        } else if (pictures == null) {
            pictures = new Picture[template.getImagesCount()];
        }
    }

    public void setTemplateEmpty(PageTemplate template) {
        pictures = new Picture[template.getImagesCount()];
        this.template = template;
    }

    public Picture[] getPictures() {
        return pictures;
    }

    public void setPictures(Picture[] pictures) {
        this.pictures = pictures;
    }
}
package ru.lucky_book.database;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by demafayz on 29.08.16.
 */
public class RealmPage extends RealmObject {
    private String templateValue;
    private RealmList<RealmPicture> pictures;

    public String getTemplateValue() {
        return templateValue;
    }

    public void setTemplateValue(String templateValue) {
        this.templateValue = templateValue;
    }

    public RealmList<RealmPicture> getPictures() {
        return pictures;
    }

    public void setPictures(RealmList<RealmPicture> pictures) {
        this.pictures = pictures;
    }
}

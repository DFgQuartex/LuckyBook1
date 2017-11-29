package ru.lucky_book.features.albumcreate.choosecover;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by demafayz on 25.08.16.
 */
public class ChooseCoverItem implements Parcelable {
    private String icon;
    private String title;
    private List<ChooseCoverItem> subItems;
    private int id;

    public String getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(String originalImage) {
        this.originalImage = originalImage;
    }

    private String originalImage;

    public ChooseCoverItem(String icon, String title,String urlSubItems,int id) {
        this.icon = icon;
        this.title = title;
        this.urlSubitems = urlSubItems;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChooseCoverItem(String thump, String original, int id) {
        this.icon = thump;
        this.originalImage = original;
        this.id = id;

    }

    public ChooseCoverItem() {

    }

    protected ChooseCoverItem(Parcel in) {
        icon = in.readString();
        title = in.readString();
        subItems = in.createTypedArrayList(ChooseCoverItem.CREATOR);
        originalImage = in.readString();
        urlSubitems = in.readString();
    }

    public static final Creator<ChooseCoverItem> CREATOR = new Creator<ChooseCoverItem>() {
        @Override
        public ChooseCoverItem createFromParcel(Parcel in) {
            return new ChooseCoverItem(in);
        }

        @Override
        public ChooseCoverItem[] newArray(int size) {
            return new ChooseCoverItem[size];
        }
    };

    public String getUrlSubitems() {
        return urlSubitems;
    }

    public void setUrlSubitems(String urlSubitems) {
        this.urlSubitems = urlSubitems;
    }

    private String urlSubitems;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ChooseCoverItem> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<ChooseCoverItem> subItems) {
        this.subItems = subItems;
    }

    public void addSubItem(ChooseCoverItem subItem) {
        if (subItems == null) subItems = new ArrayList<>();
        subItem.addSubItem(subItem);
    }

    public ChooseCoverItem getSubItem(int position) {
        if (subItems == null) return null;
        return subItems.get(position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(icon);
        parcel.writeString(title);
        parcel.writeTypedList(subItems);
        parcel.writeString(originalImage);
        parcel.writeString(urlSubitems);
    }
}
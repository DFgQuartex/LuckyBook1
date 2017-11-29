package me.nereo.multi_image_selector.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 图片实体
 * Created by Nereo on 2015/4/7.
 */
public class Image implements Serializable {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_EMPTY = 1;

    public String path;
    public String thumbnailPath;
    public String name;
    public long time;
    public int width;
    public int height;
    public boolean isLocal;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Image(int type) {
        this.type = type;
    }

    public int type = TYPE_IMAGE;


    public Image(String name, String path, String thumbnailPath, long time, int width, int height, boolean isLocal) {
        this.name = name;
        this.path = path;
        this.thumbnailPath = thumbnailPath;
        this.time = time;
        this.width = width;
        this.height = height;
        this.isLocal = isLocal;
    }


    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return TextUtils.equals(this.path, other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}

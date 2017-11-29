package com.example.luckybookpreview.utils;

/**
 * Created by ilgiz on 6/30/16.
 */
public class Dimensions {

    private final int mWidth;
    private final int mHeight;

    public Dimensions(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public static final int TOO_SHORT = -1;
    public static final int TOO_NARROW = 1;
    public static final int OK = 0;

    /**
     * Checks if these <code>Dimensions</code> are at least the given width and height.
     * <ul>
     * <li><b>too short</b> means that the height is less than <code>minHeight</code>
     * <li><b>too narrow</b> means that the width is less than <code>minWidth</code>
     * </ul>
     *
     * @param minWidth
     * @param minHeight
     * @return
     */
    public int atLeast(int minWidth, int minHeight) {
        if (mWidth < minWidth) {
            return TOO_NARROW;
        }
        if (mHeight < minHeight) {
            return TOO_SHORT;
        }
        return OK;
    }

    public int atLeast(Dimensions minDimensions) {
        return atLeast(minDimensions.getWidth(), minDimensions.getHeight());
    }

    @Override
    public String toString() {
        return "Dimensions{" +
                "mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                '}';
    }
}

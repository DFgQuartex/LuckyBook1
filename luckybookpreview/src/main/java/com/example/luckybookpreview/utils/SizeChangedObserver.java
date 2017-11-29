package com.example.luckybookpreview.utils;

import android.app.Activity;

import com.example.luckybookpreview.ui.views.CurlView;

public class SizeChangedObserver implements CurlView.SizeChangedObserver {

    private int width;
    private int height;
    private float percent;

    public SizeChangedObserver(Activity activity, int width, int height) {
        this.width = width;
        this.height = height;
        percent = ((float) 1 - DispUtil.getMarginPercent(activity, width, height, false)) / (float) 1.7;
    }

    @Override
    public void onSizeChanged(CurlView curlView, int w, int h) {
        if (w > h) {
            curlView.setViewMode(CurlView.SHOW_TWO_PAGES);
            curlView.setMargins(.2f, .2f, .2f, .2f);
        } else {
            curlView.setViewMode(CurlView.SHOW_ONE_PAGE);
            float margin = getPadding(width, height, w, h);
            curlView.setMargins(.0f, percent, .0f, percent);
        }
    }

    public float getPadding(int imW, int imH, int vW, int vH) {
        int x1, x2, y1, y2, newX, newY;
        x1 = vH;
        y1 = vW;
        x2 = imH;
        y2 = imW;

        float k1, k2, k, razn;
        k1 = ((float) x1 / (float) x2);
        k2 = ((float) y1 / (float) y2);

        if (k1 < k2) {
            k = k1;
        } else {
            k = k2;
        }

        newX = (int) (x2 * k);
        newY = (int) (y2 * k);

        razn = ((float) newX / (float) x1) / (float) 2;
        razn = razn / (float) 0.75;
        return razn;
    }
}
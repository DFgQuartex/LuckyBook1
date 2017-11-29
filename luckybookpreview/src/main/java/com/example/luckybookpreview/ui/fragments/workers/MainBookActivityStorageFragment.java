package com.example.luckybookpreview.ui.fragments.workers;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DemaFayz on 30.06.2016.
 */
public class MainBookActivityStorageFragment extends Fragment {

    public static final String WORKER_TAG = MainBookActivityStorageFragment.class.getCanonicalName() + "_WORKER_TAG";

    private boolean orientation;
    private List<Bitmap> slides;

    public MainBookActivityStorageFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public List<Bitmap> getSlides() {
        return slides;
    }

    public boolean getOrientation() {
        return orientation;
    }

    private void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    private void setSlides(List<Bitmap> slides) {
        this.slides = slides;
    }

    public static MainBookActivityStorageFragment newInstance(List<Bitmap> slides, boolean orientation) {
        MainBookActivityStorageFragment fragment = new MainBookActivityStorageFragment();
        fragment.setOrientation(orientation);
        fragment.setSlides(slides);
        return fragment;
    }
}

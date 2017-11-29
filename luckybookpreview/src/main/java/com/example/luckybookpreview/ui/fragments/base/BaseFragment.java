package com.example.luckybookpreview.ui.fragments.base;

import android.support.v4.app.Fragment;

/**
 * Created by DemaFayz on 29.06.2016.
 */
public abstract class BaseFragment extends Fragment {
    public String tag = initTag();

    protected abstract String initTag();
}

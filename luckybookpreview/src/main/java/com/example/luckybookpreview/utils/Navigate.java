package com.example.luckybookpreview.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.luckybookpreview.R;

/**
 * Created by DemaFayz on 29.06.2016.
 */
public class Navigate {

    public static void showFragment(FragmentManager manager, Fragment fragment, boolean useBackStack) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (useBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }
}

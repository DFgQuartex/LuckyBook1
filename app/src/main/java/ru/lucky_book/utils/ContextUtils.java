package ru.lucky_book.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import ru.lucky_book.app.GreetingActivity;

/**
 * Created by demafayz on 23.08.16.
 */
public class ContextUtils {

    public static void openMainScreen(Context context) {
        Intent intent = new Intent(context, GreetingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void showNotification(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void setToolBar(AppCompatActivity activity, Toolbar toolbar) {
        activity.setSupportActionBar(toolbar);
    }

    public static String getStringResourceByName(Context context,String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }

    public static void showFragment(int fragmentContainerId, FragmentActivity activity, Fragment fragment, boolean addBackStack) {
        FragmentManager fm;
        FragmentTransaction ft;
        fm = activity.getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(fragmentContainerId, fragment);
        if (addBackStack) ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }
}

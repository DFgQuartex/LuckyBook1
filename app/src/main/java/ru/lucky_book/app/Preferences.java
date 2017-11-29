package ru.lucky_book.app;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    private static final String TAG = Preferences.class.getSimpleName();

    public static class Payment {
        private static final String PREFERENCES_NAME = String.format("%s.%s", TAG, Payment.class.getSimpleName());

        public static boolean isPaid(Context context, String albumId) {
            return getPreferences(context).getBoolean(albumId, false);
        }

        public static void setPaid(Context context, String albumId, boolean paid) {
            getEditor(context).putBoolean(albumId, paid).commit();
        }

        public static void removePaid(Context context, String albumId) {
            getEditor(context).remove(albumId).commit();
        }

        private static SharedPreferences getPreferences(Context context) {
            return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }

        private static SharedPreferences.Editor getEditor(Context context) {
            return getPreferences(context).edit();
        }
    }

    public static class OrderData {
        public static final int DEFAULT_ORDER_ID = -1;
        private static final String PREFERENCES_NAME = TAG + '.' + OrderData.class.getSimpleName();
        public static final String CITY = "city";
        public static final String STREET = "street";
        public static final String HOUSE = "house";
        public static final String FLAT = "flat";
        public static final String ZIP = "zip";
        public static final String NAME = "name";
        public static final String SURNAME = "surname";
        public static final String PATRONYMIC = "patronymic";
        public static final String BIRTHDAY = "birthday";
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";
        public static final String ORDER_ID = "orderid";

        public static String getProperty(Context context, String key) {
            return getPreferences(context).getString(key, null);
        }

        public static void setProperty(Context context, String key, String value) {
            getEditor(context).putString(key, value).commit();
        }

        public static int getOrderId(Context context, String key) {
            return getPreferences(context).getInt(ORDER_ID + key, DEFAULT_ORDER_ID);
        }

        public static void setOrderId(Context context, String key, int value) {
            getEditor(context).putInt(ORDER_ID + key, value).commit();
        }

        public static void removeProperty(Context context, String key) {
            getEditor(context).remove(key).commit();
        }

        private static SharedPreferences getPreferences(Context context) {
            return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }

        private static SharedPreferences.Editor getEditor(Context context) {
            return getPreferences(context).edit();
        }
    }

    public static class Tutorial {
        private static final String PREFERENCES_NAME = TAG + '.' + Tutorial.class.getSimpleName();
        public static final String FIRST_CHANGE_SCALE_RUN = "first_change_scale_run";

        public static boolean getProperty(Context context, String key) {
            return getPreferences(context).getBoolean(key, false);
        }

        public static void setProperty(Context context, String key, boolean value) {
            getEditor(context).putBoolean(key, value).commit();
        }

        private static SharedPreferences getPreferences(Context context) {
            return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }

        private static SharedPreferences.Editor getEditor(Context context) {
            return getPreferences(context).edit();
        }
    }

    public static class Instructions {
        private static final String PREFERENCES_NAME = TAG + '.' + Instructions.class.getSimpleName();
        public static final String FIRST_START_APP = "first";

        public static void firstStartDone() {
            getEditor().putBoolean(FIRST_START_APP, true).commit();
        }

        public static boolean isFirstStartDone() {
            return getPreferences().getBoolean(FIRST_START_APP, false);
        }

        private static SharedPreferences getPreferences() {
            return mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }

        private static SharedPreferences.Editor getEditor() {
            return getPreferences().edit();
        }
    }

    public static class Instagram {

        private static final String PREFERENCES_NAME = "Preferences.Instagram";
        private static final String COOKIE = "cookie";
        private static final String USERNAME = "username";

        private static final String ACCESS_TOKEN = "access_token";

        public static String getAccessToken(Context context) {
            return getPreferences(context).getString(ACCESS_TOKEN, null);
        }

        public static void setAccessToken(Context context, String token) {
            getEditor(context).putString(ACCESS_TOKEN, token).commit();
        }

        public static String getCookie(Context context) {
            return getPreferences(context).getString(COOKIE, null);
        }

        public static void setCookie(Context context, String cookie) {
            getEditor(context).putString(COOKIE, cookie).commit();
        }

        public static String getUsername(Context context) {
            return getPreferences(context).getString(USERNAME, null);
        }

        public static void setUsername(Context context, String username) {
            getEditor(context).putString(USERNAME, username).commit();
        }

        private static SharedPreferences getPreferences(Context context) {
            return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }

        private static SharedPreferences.Editor getEditor(Context context) {
            return getPreferences(context).edit();
        }
    }
}

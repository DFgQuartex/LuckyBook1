package ru.lucky_book.features.preview_screen.utils;

import android.accounts.Account;
import android.app.Activity;
import android.support.v4.content.AsyncTaskLoader;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.IOException;

public class GoogleDriveLoader extends AsyncTaskLoader<String> {

    public static final int REQ_SIGN_IN_REQUIRED = 200;

    private String mLogin;
    private String mScopes = "oauth2:" + DriveScopes.DRIVE_APPDATA + " " + DriveScopes.DRIVE + " " + DriveScopes.DRIVE_FILE;
    private File mFile;
    private Activity mActivity;

    public GoogleDriveLoader(Activity activity, String login, File file) {
        super(activity.getApplicationContext());
        mLogin = login;
        mFile = file;
        mActivity = activity;
    }

    @Override
    public String loadInBackground() {
        Account account = new Account(mLogin, "com.google");
        try {
            String token = GoogleAuthUtil.getToken(getContext(), account, mScopes);
            if (token != null) {
                String link = GoogleDriveHelper.newInstance(getContext(), token).uploadFile(mFile,null);
                return link;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UserRecoverableAuthException e) {
            mActivity.startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            return "";
        } catch(GoogleAuthException e) {
            e.printStackTrace();
        }
        return null;
    }
}

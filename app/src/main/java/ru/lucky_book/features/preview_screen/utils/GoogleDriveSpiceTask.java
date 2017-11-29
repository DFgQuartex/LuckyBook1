package ru.lucky_book.features.preview_screen.utils;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.services.drive.DriveScopes;
import com.octo.android.robospice.request.SpiceRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import ru.lucky_book.utils.listeners.DriveFileUploadProgressListener;

/**
 * Created by histler
 * on 02.09.16 17:54.
 */
public class GoogleDriveSpiceTask extends SpiceRequest<String> implements DriveFileUploadProgressListener {
    private Context mContext;
    private String mLogin;
    private String mScopes = "oauth2:" + DriveScopes.DRIVE_APPDATA + " " + DriveScopes.DRIVE + " " + DriveScopes.DRIVE_FILE;
    private File mFile;
    private WeakReference<InputStream> mStream;
    public GoogleDriveSpiceTask(Context context,String login, File file) {
        super(String.class);
        mContext=context.getApplicationContext();
        mLogin=login;
        mFile=file;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        Account account = new Account(mLogin, "com.google");
        String token = GoogleAuthUtil.getToken(mContext, account, mScopes);
        if (token != null) {
            String link = GoogleDriveHelper.newInstance(mContext, token).uploadFile(mFile,this);
            return link;
        }
        return null;
    }

    @Override
    public void cancel() {
        super.cancel();
        if(mStream.get()!=null){
            try {
                mStream.get().close();
            } catch (IOException e) {
                Log.d(getClass().getSimpleName(),e.getMessage());
            }
        }
    }

    @Override
    public void progressChanged(MediaHttpUploader uploader) throws IOException {
        Log.d(GoogleDriveSpiceTask.class.getSimpleName(),"uploader progress: "+uploader.getNumBytesUploaded());
        publishProgress((float)uploader.getNumBytesUploaded());
    }

    @Override
    public void onStreamSet(InputStream inputStream) {
        mStream=new WeakReference<>(inputStream);
    }
}

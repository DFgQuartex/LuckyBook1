package ru.lucky_book.network.robospice.dropbox;

import android.content.Context;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.octo.android.robospice.request.SpiceRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Date;

import ru.lucky_book.features.order_screen.dropbox_integration.DropboxHelper;
import ru.lucky_book.features.order_screen.dropbox_integration.exception.ChunkSizeSmallException;

public class DropboxSpiceTask extends SpiceRequest<String> implements DropboxHelper.ProgressListener {

    public static final int ID = 200;

    private File mFile;
    private Context mContext;
    private String mUserFullName;
    private WeakReference<InputStream> mInputStreamWeakReference;

    public DropboxSpiceTask(Context context, File file, String userFullName) {
        super(String.class);
        mContext = context;
        mFile = file;
        mUserFullName = userFullName;
    }

    @Override
    public String loadDataFromNetwork() {
        String link = null;
        DropboxHelper dropboxHelper = null;
        try {
            dropboxHelper = DropboxHelper.newInstance(mContext, new Date(), mUserFullName);
            return link = dropboxHelper.chunkUpload(mFile, this);
        } catch (ChunkSizeSmallException e) {
            return link = dropboxHelper.uploadFile(mFile);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void cancel() {
        super.cancel();
        if(mInputStreamWeakReference.get()!=null){
            try {
                mInputStreamWeakReference.get().close();
            } catch (IOException e) {
                Log.d(getClass().getSimpleName(),e.getMessage());
            }
        }
    }

    @Override
    public void onStreamSet(InputStream inputStream) {
        mInputStreamWeakReference = new WeakReference<>(inputStream);
    }

    @Override
    public void onProgress(double progress) {
        Log.d(DropboxSpiceTask.class.getSimpleName(),"uploader progress: "+progress);
        publishProgress((float) progress);
    }
}
